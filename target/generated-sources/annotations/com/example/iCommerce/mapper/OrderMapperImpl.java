package com.example.iCommerce.mapper;

import com.example.iCommerce.dto.request.CartRequest;
import com.example.iCommerce.dto.request.OrderNowRequest;
import com.example.iCommerce.dto.request.OrderRequest;
import com.example.iCommerce.dto.response.OrderResponse;
import com.example.iCommerce.entity.Cart;
import com.example.iCommerce.entity.Order;
import com.example.iCommerce.entity.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Amazon.com Inc.)"
)
@Component
public class OrderMapperImpl implements OrderMapper {

    @Override
    public Order toOrder(OrderRequest request) {
        if ( request == null ) {
            return null;
        }

        Order.OrderBuilder order = Order.builder();

        order.name( request.getName() );
        order.address( request.getAddress() );
        order.fulladdress( request.getFulladdress() );
        order.note( request.getNote() );
        order.phone( request.getPhone() );
        order.payment( request.getPayment() );
        order.device( request.getDevice() );

        return order.build();
    }

    @Override
    public Order toOrderNow(OrderNowRequest request) {
        if ( request == null ) {
            return null;
        }

        Order.OrderBuilder order = Order.builder();

        order.name( request.getName() );
        order.amount( request.getAmount() );
        order.address( request.getAddress() );
        order.fulladdress( request.getFulladdress() );
        order.note( request.getNote() );
        order.phone( request.getPhone() );
        order.payment( request.getPayment() );
        order.device( request.getDevice() );
        order.carts( cartRequestListToCartList( request.getCarts() ) );

        return order.build();
    }

    @Override
    public OrderResponse toOrderResponse(Order order) {
        if ( order == null ) {
            return null;
        }

        OrderResponse.OrderResponseBuilder orderResponse = OrderResponse.builder();

        orderResponse.user_id( orderUserId( order ) );
        orderResponse.id( order.getId() );
        orderResponse.name( order.getName() );
        orderResponse.amount( order.getAmount() );
        orderResponse.address( order.getAddress() );
        orderResponse.fulladdress( order.getFulladdress() );
        orderResponse.note( order.getNote() );
        orderResponse.phone( order.getPhone() );
        orderResponse.date( order.getDate() );
        orderResponse.payment( order.getPayment() );
        orderResponse.device( order.getDevice() );

        return orderResponse.build();
    }

    protected Cart cartRequestToCart(CartRequest cartRequest) {
        if ( cartRequest == null ) {
            return null;
        }

        Cart.CartBuilder cart = Cart.builder();

        cart.price( cartRequest.getPrice() );
        cart.quantity( cartRequest.getQuantity() );

        return cart.build();
    }

    protected List<Cart> cartRequestListToCartList(List<CartRequest> list) {
        if ( list == null ) {
            return null;
        }

        List<Cart> list1 = new ArrayList<Cart>( list.size() );
        for ( CartRequest cartRequest : list ) {
            list1.add( cartRequestToCart( cartRequest ) );
        }

        return list1;
    }

    private String orderUserId(Order order) {
        if ( order == null ) {
            return null;
        }
        User user = order.getUser();
        if ( user == null ) {
            return null;
        }
        String id = user.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
