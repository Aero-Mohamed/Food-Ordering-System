package com.food.order.service.domain.mapper;

import com.food.domain.valueobject.CustomerId;
import com.food.domain.valueobject.Money;
import com.food.domain.valueobject.ProductId;
import com.food.domain.valueobject.RestaurantId;
import com.food.order.service.domain.dto.create.CreateOrderCommand;
import com.food.order.service.domain.dto.create.CreateOrderResponse;
import com.food.order.service.domain.dto.create.OrderAddress;
import com.food.order.service.domain.dto.track.TrackOrderResponse;
import com.food.order.service.domain.entity.Order;
import com.food.order.service.domain.entity.OrderItem;
import com.food.order.service.domain.entity.Product;
import com.food.order.service.domain.entity.Restaurant;
import com.food.order.service.domain.valueobject.StreetAddress;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class OrderDataMapper {

    public Restaurant createOrderCommandToRestaurant(CreateOrderCommand createOrderCommand) {
        return Restaurant.builder()
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .products(
                    createOrderCommand.getItems().stream().map(orderItem ->
                        new Product(new ProductId(orderItem.getProductId()))
                    ).toList()
                )
                .build();
    }

    public Order createOrderCommandToOrder(CreateOrderCommand command) {
        return Order.builder()
                .customerId(new CustomerId(command.getCustomerId()))
                .restaurantId(new RestaurantId(command.getRestaurantId()))
                .streetAddress(orderAddresstoStreetAddress(command.getAddress()))
                .price(new Money(command.getPrice()))
                .items(orderItemsToOrderItemEntities(command.getItems()))
                .build();
    }

    public CreateOrderResponse orderToCreateOrderResponse(Order order, String message) {
        return CreateOrderResponse.builder()
                .orderTrackingId(order.getTrackingId().getValue())
                .orderStatus(order.getOrderStatus())
                .message(message)
                .build();
    }

    public TrackOrderResponse orderToTrackOrderResponse(Order order) {
        return TrackOrderResponse.builder()
                .orderTrackingId(order.getTrackingId().getValue())
                .orderStatus(order.getOrderStatus())
                .message(order.getFailureMessages())
                .build();
    }

    private List<OrderItem> orderItemsToOrderItemEntities(
            @NotNull List<com.food.order.service.domain.dto.create.OrderItem> orderItems) {
        return orderItems.stream().map(orderItem ->
            OrderItem.builder()
                .product(new Product(new ProductId(orderItem.getProductId())))
                .price(new Money(orderItem.getPrice()))
                .quantity(orderItem.getQuantity())
                .subTotal(new Money(orderItem.getSubTotal()))
                .build()
        ).toList();
    }

    private StreetAddress orderAddresstoStreetAddress(OrderAddress address) {
        return new StreetAddress(
            UUID.randomUUID(),
            address.getStreet(),
            address.getCity(),
            address.getPostalCode()
        );
    }

}
