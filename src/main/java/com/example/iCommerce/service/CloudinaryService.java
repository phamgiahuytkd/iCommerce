package com.example.iCommerce.service;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.iCommerce.dto.request.AddressRequest;
import com.example.iCommerce.dto.response.AddressResponse;
import com.example.iCommerce.entity.Address;
import com.example.iCommerce.entity.CloudinaryImage;
import com.example.iCommerce.entity.User;
import com.example.iCommerce.exception.AppException;
import com.example.iCommerce.exception.ErrorCode;
import com.example.iCommerce.mapper.AddressMapper;
import com.example.iCommerce.repository.AddressRepository;
import com.example.iCommerce.repository.CloudinaryImageRepository;
import com.example.iCommerce.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CloudinaryService {

    final CloudinaryImageRepository cloudinaryImageRepository;
    final Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", System.getenv("CLOUDINARY_CLOUD_NAME"),
            "api_key", System.getenv("CLOUDINARY_API_KEY"),
            "api_secret", System.getenv("CLOUDINARY_API_SECRET")
    ));

    public String upload(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("folder", "iCommerce/images"));

        CloudinaryImage cloudinaryImage = CloudinaryImage.builder()
                .id(uploadResult.get("secure_url").toString())
                .public_id(uploadResult.get("public_id").toString())
                .build();
        cloudinaryImageRepository.save(cloudinaryImage);
        return uploadResult.get("secure_url").toString();
    }

    public String update(String secureUrl, MultipartFile file) throws IOException {
        // 1. Tìm entity cũ
        cloudinaryImageRepository.findById(secureUrl).ifPresent(oldImage -> {
            try {
                // Xóa ảnh cũ trên Cloudinary
                if(oldImage.getPublic_id() != null && !oldImage.getPublic_id().isEmpty()) {
                    cloudinary.uploader().destroy(oldImage.getPublic_id(), ObjectUtils.emptyMap());
                }
                // Xóa record cũ trong DB
                cloudinaryImageRepository.delete(oldImage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // 2. Upload ảnh mới
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("folder", "iCommerce/images"));

        // 3. Tạo record mới
        CloudinaryImage newImage = new CloudinaryImage();
        newImage.setId(uploadResult.get("secure_url").toString());  // nếu vẫn dùng URL làm ID
        newImage.setPublic_id(uploadResult.get("public_id").toString());
        cloudinaryImageRepository.save(newImage);

        return uploadResult.get("secure_url").toString();
    }


    public void delete(String secureUrl) throws IOException {
        // 1. Tìm trong DB
        CloudinaryImage cloudinaryImage = cloudinaryImageRepository.findById(secureUrl)
                .orElse(new CloudinaryImage());

        // 2. Xóa ảnh trên Cloudinary
        if (cloudinaryImage.getPublic_id() != null && !cloudinaryImage.getPublic_id().isEmpty()) {
            cloudinary.uploader().destroy(cloudinaryImage.getPublic_id(), ObjectUtils.emptyMap());
        }

        // 3. Xóa record trong DB
        cloudinaryImageRepository.deleteById(secureUrl);
    }

}

