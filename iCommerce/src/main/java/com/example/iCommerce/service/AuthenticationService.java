package com.example.iCommerce.service;

import com.example.iCommerce.dto.request.AuthenticationRequest;
import com.example.iCommerce.dto.request.IntrospectRequest;
import com.example.iCommerce.dto.request.LogoutRequest;
import com.example.iCommerce.dto.request.RefreshRequest;
import com.example.iCommerce.dto.response.AuthenticationResponse;
import com.example.iCommerce.dto.response.IntrospectResponse;
import com.example.iCommerce.entity.InvalidatedToken;
import com.example.iCommerce.entity.User;
import com.example.iCommerce.enums.ActionKey;
import com.example.iCommerce.exception.AppException;
import com.example.iCommerce.exception.ErrorCode;
import com.example.iCommerce.repository.InvalidatedTokenRepository;
import com.example.iCommerce.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {


    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;


    UserRepository userRepository;

    InvalidatedTokenRepository invalidatedTokenRepository;

    TrackingService trackingService;



    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();

        var jwtToken = request.getToken();

        boolean invalid = true;

        try {
            verifyToken(jwtToken, false);


        }catch (AppException e){
            invalid = false;
        }

        return IntrospectResponse.builder()
                .valid(invalid)
                .build();


    }



    public AuthenticationResponse authenticate(AuthenticationRequest request){
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_EXISTED));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if(!authenticated)
            throw new AppException(ErrorCode.PASSWORD_INCORRECT);

        var token = generateToken(user);


        trackingService.tracking(user.getId(), ActionKey.LOGIN.name(), "User đăng nhập vào trang web");

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }



    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier jwsVerifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified =  signedJWT.verify(jwsVerifier);

        if(!(verified && expiryTime.after(new Date())))
            throw new AppException(ErrorCode.UNAUTHENTICATED);


        if(invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);


        return signedJWT;
    }



    private String generateToken(User user){
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId())
                .issuer("iCommerce.com")
                .issueTime(new Date())
                .expirationTime( new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()
                ))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", user.getUser_type())
                .build();

        Payload payload = new Payload(claimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }

    }




    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        var signToken = verifyToken(request.getToken(), true);

        String jit = signToken.getJWTClaimsSet().getJWTID();
        Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

        String created_by = signToken.getJWTClaimsSet().getSubject();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expiryTime(expiryTime)
                .build();

        trackingService.tracking(created_by, ActionKey.LOGOUT.name(), "User đăng xuất khỏi trang web");

        invalidatedTokenRepository.save(invalidatedToken);
    }


    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        var signJWT = verifyToken(request.getToken(), true);
        var jit = signJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expiryTime(expiryTime)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);

        var id = signJWT.getJWTClaimsSet().getSubject();

        var user = userRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.UNAUTHENTICATED)
        );

        var token = generateToken(user);

        trackingService.tracking(user.getId(), ActionKey.LOGIN.name(), "User đăng nhập vào trang web");

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();



    }

}
