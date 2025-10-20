package com.example.iCommerce.service;


import com.example.iCommerce.client.MomoApi;
import com.example.iCommerce.constant.MomoParameter;
import com.example.iCommerce.dto.request.CreateMomoRequest;
import com.example.iCommerce.dto.request.MoMoMethodRequest;
import com.example.iCommerce.dto.response.CreateMomoResponse;
import com.example.iCommerce.entity.Notify;
import com.example.iCommerce.entity.Order;
import com.example.iCommerce.entity.OrderStatus;
import com.example.iCommerce.entity.User;
import com.example.iCommerce.enums.NotifyType;
import com.example.iCommerce.exception.AppException;
import com.example.iCommerce.exception.ErrorCode;
import com.example.iCommerce.repository.NotifyRepository;
import com.example.iCommerce.repository.OrderRepository;
import com.example.iCommerce.repository.OrderStatusRepository;
import com.example.iCommerce.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MomoService {
    private final OrderRepository orderRepository;
    @Value("${momo.partner-code}")
    private String PARTNER_CODE;

    @Value("${momo.access-key}")
    private String ACCESS_KEY;

    @Value("${momo.secret-key}")
    private String SECRET_KEY;

    @Value("${momo.return-url}")
    private String REDIRECT_URL;

    @Value("${momo.ipn-url}")
    private String IPN_URL;

    @Value("${momo.request-type}")
    private String REQUEST_TYPE;


    private final MomoApi momoApi;
    private final OrderStatusRepository orderStatusRepository;
    SimpMessagingTemplate messagingTemplate;
    NotifyRepository notifyRepository;
    UserRepository userRepository;


    public String ipnHandler(Map<String,String> request){
        log.info(">>> IPN nhận được với data: {}", request);
        Integer resultCode = Integer.valueOf(request.get(MomoParameter.RESULT_CODE));
        String orderId = String.valueOf(request.get(MomoParameter.ORDER_INFO));
        if(resultCode == 0){
            Order order = orderRepository.findById(orderId).orElseThrow(
                    ()-> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION)
            );
            OrderStatus orderStatus = OrderStatus.builder()
                    .order(order)
                    .status(com.example.iCommerce.enums.OrderStatus.PAID.name())
                    .description("Đã thanh toán qua MoMo.")
                    .update_day(LocalDateTime.now())
                    .build();
            orderStatusRepository.save(orderStatus);

            // 🔹 6️⃣ Gửi thông báo cho admin
            User admin = userRepository.findByEmail("admin@gmail.com").orElseThrow(
                    () -> new AppException(ErrorCode.USER_NOT_EXISTED)
            );


            Notify notify = Notify.builder()
                    .title("Đã thanh toán")
                    .type(NotifyType.ORDER.name())
                    .type_id(order.getId())
                    .message("Khách hàng " + order.getUser().getFull_name() + " vừa thanh toán đơn #" + order.getId())
                    .create_day(LocalDateTime.now())
                    .user(admin)
                    .build();

            notifyRepository.save(notify);

            messagingTemplate.convertAndSend("/topic/admin", com.example.iCommerce.enums.OrderStatus.PAID.name());

            return "Giao dịch thành công.";

        }else {
            return "Giao dịch thất bại.";
        }


    }

    public CreateMomoResponse createMomoQR(MoMoMethodRequest request){
        String extraData = "khong co khuyen mai gi ca";

        String rawSignature = String.format(
                "accessKey=%s&amount=%s&extraData=%s&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=%s",
                ACCESS_KEY,
                request.getAmount(),
                extraData,
                IPN_URL,
                request.getOrderId(),
                request.getOrderInfo(),
                PARTNER_CODE,
                REDIRECT_URL,
                request.getRequestId(),
                REQUEST_TYPE
        );



        String prettySignature = "";
        try{
            prettySignature = signHmacSHA256(rawSignature, SECRET_KEY);
        } catch (Exception e) {
            log.error(">>>>>Co loi hash code " + e);
            throw new RuntimeException(e);
        }


        if(prettySignature.isBlank()){
            log.error(">>>Co loi blank");
            return null;
        }


        CreateMomoRequest momo = CreateMomoRequest.builder()
                .partnerCode(PARTNER_CODE)
                .requestType(REQUEST_TYPE)
                .ipnUrl(IPN_URL)
                .redirectUrl(REDIRECT_URL)
                .orderId(request.getOrderId())
                .orderInfo(request.getOrderInfo())
                .requestId(request.getRequestId())
                .extraData(extraData)
                .amount(request.getAmount())
                .signature(prettySignature)
                .lang("vi")
                .build();

        return momoApi.createMomoQR(momo);

    }


    private String signHmacSHA256(String data, String key) throws Exception {
        Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmacSHA256.init(secretKey);

        byte[] hash = hmacSHA256.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();

        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }




}
