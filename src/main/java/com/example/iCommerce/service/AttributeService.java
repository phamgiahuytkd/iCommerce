package com.example.iCommerce.service;


import com.example.iCommerce.dto.request.AddressRequest;
import com.example.iCommerce.dto.request.AttributeRequest;
import com.example.iCommerce.dto.request.AttributeValueRequest;
import com.example.iCommerce.dto.response.AddressResponse;
import com.example.iCommerce.dto.response.AttributeResponse;
import com.example.iCommerce.entity.Address;
import com.example.iCommerce.entity.Attribute;
import com.example.iCommerce.entity.AttributeValue;
import com.example.iCommerce.entity.User;
import com.example.iCommerce.exception.AppException;
import com.example.iCommerce.exception.ErrorCode;
import com.example.iCommerce.mapper.AddressMapper;
import com.example.iCommerce.mapper.AttributeMapper;
import com.example.iCommerce.repository.AddressRepository;
import com.example.iCommerce.repository.AttributeRepository;
import com.example.iCommerce.repository.AttributeValueRepository;
import com.example.iCommerce.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttributeService {
    AttributeRepository  attributeRepository;
    AttributeMapper attributeMapper;
    AttributeValueRepository attributeValueRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public void createAttribute(AttributeRequest request) {

       if(attributeRepository.existsByName(request.getName().trim())){
           throw new AppException(ErrorCode.ATTRIBUTE_EXISTED);
       }

        // Tạo Attribute (chưa có value)
        Attribute attribute = Attribute.builder()
                .name(request.getName().trim())
                .build();

        // Tạo AttributeValue từ danh sách values trong request
        List<AttributeValue> valueEntities = request.getValues().stream()
                .map(valueName -> AttributeValue.builder()
                        .name(valueName.trim())
                        .attribute(attribute) // Gán mối quan hệ ngược
                        .build())
                .toList();

        // Gán danh sách vào attribute
        attribute.setAttributeValues(valueEntities);

        // Lưu toàn bộ (vì cascade = CascadeType.ALL)
        attributeRepository.save(attribute);
    }


    @PreAuthorize("hasRole('ADMIN')")
    public void createAttributeValue(AttributeValueRequest request) {
        Attribute attribute = attributeRepository.findById(request.getAttribute_id().trim())
                .orElseThrow(() -> new AppException(ErrorCode.ATTRIBUTE_NOT_EXISTED));

        // Kiểm tra nếu một trong các name đã tồn tại
        if (attributeValueRepository.existsByNameIn(request.getValues())) {
            throw new AppException(ErrorCode.ATTRIBUTE_EXISTED);
        }

        // Tạo danh sách các AttributeValue từ request.values
        List<AttributeValue> valuesToSave = request.getValues().stream()
                .map(name -> AttributeValue.builder()
                        .name(name.trim())
                        .attribute(attribute)
                        .build())
                .toList();

        // Lưu tất cả
        attributeValueRepository.saveAll(valuesToSave);
    }



    @PreAuthorize("hasRole('ADMIN')")
    public List<AttributeResponse> getAttributes() {
        List<Attribute> attributes = attributeRepository.findAll();
        return attributeMapper.toAttributeResponseList(attributes);
    }































}
