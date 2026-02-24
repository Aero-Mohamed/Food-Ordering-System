package com.food.order.service.domain;

import com.food.order.service.domain.dto.message.RestaurantApproveResponse;
import com.food.order.service.domain.ports.input.message.listener.restaurantapproval.RestaurantApprovalResponseMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
public class RestaurantApprovalResponseMessageListenerImpl implements RestaurantApprovalResponseMessageListener {
    @Override
    public void orderApproved(RestaurantApproveResponse restaurantApproveResponse) {

    }

    @Override
    public void orderRejected(RestaurantApproveResponse restaurantApproveResponse) {

    }
}
