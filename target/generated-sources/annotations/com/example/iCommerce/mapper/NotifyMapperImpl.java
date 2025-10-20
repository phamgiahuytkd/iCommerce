package com.example.iCommerce.mapper;

import com.example.iCommerce.dto.response.NotifyResponse;
import com.example.iCommerce.entity.Notify;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Amazon.com Inc.)"
)
@Component
public class NotifyMapperImpl implements NotifyMapper {

    @Override
    public NotifyResponse toNotifyResponse(Notify notify) {
        if ( notify == null ) {
            return null;
        }

        NotifyResponse.NotifyResponseBuilder notifyResponse = NotifyResponse.builder();

        notifyResponse.id( notify.getId() );
        notifyResponse.title( notify.getTitle() );
        notifyResponse.type( notify.getType() );
        notifyResponse.type_id( notify.getType_id() );
        notifyResponse.message( notify.getMessage() );
        notifyResponse.create_day( notify.getCreate_day() );

        return notifyResponse.build();
    }

    @Override
    public List<NotifyResponse> toNotifyResponseList(List<Notify> notifies) {
        if ( notifies == null ) {
            return null;
        }

        List<NotifyResponse> list = new ArrayList<NotifyResponse>( notifies.size() );
        for ( Notify notify : notifies ) {
            list.add( toNotifyResponse( notify ) );
        }

        return list;
    }
}
