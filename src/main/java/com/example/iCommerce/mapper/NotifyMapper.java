package com.example.iCommerce.mapper;


import com.example.iCommerce.dto.request.AddressRequest;
import com.example.iCommerce.dto.response.AddressResponse;
import com.example.iCommerce.dto.response.NotifyResponse;
import com.example.iCommerce.entity.Address;
import com.example.iCommerce.entity.Notify;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotifyMapper {
    NotifyResponse toNotifyResponse(Notify notify);
    List<NotifyResponse> toNotifyResponseList(List<Notify> notifies);

}
