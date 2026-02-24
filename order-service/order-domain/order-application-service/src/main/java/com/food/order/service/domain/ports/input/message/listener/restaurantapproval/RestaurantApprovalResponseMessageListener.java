package com.food.order.service.domain.ports.input.message.listener.restaurantapproval;

import com.food.order.service.domain.dto.message.RestaurantApproveResponse;

public interface RestaurantApprovalResponseMessageListener {
    void orderApproved(RestaurantApproveResponse restaurantApproveResponse);
    void orderRejected(RestaurantApproveResponse restaurantApproveResponse);
}
