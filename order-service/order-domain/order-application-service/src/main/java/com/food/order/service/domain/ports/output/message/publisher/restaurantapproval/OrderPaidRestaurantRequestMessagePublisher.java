package com.food.order.service.domain.ports.output.message.publisher.restaurantapproval;

import com.food.domain.event.publisher.DomainEventPublisher;
import com.food.order.service.domain.event.OrderPaidEvent;

public interface OrderPaidRestaurantRequestMessagePublisher extends DomainEventPublisher<OrderPaidEvent> {
}
