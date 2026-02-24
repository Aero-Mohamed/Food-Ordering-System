package com.food.order.service.domain.ports.output.message.publisher.payment;

import com.food.domain.event.publisher.DomainEventPublisher;
import com.food.order.service.domain.event.OrderCancelledEvent;

public interface OrderCancelledPaymentRequestMessagePublisher extends DomainEventPublisher<OrderCancelledEvent> {
}
