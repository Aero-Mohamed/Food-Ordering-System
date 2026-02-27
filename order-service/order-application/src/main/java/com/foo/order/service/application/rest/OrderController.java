package com.foo.order.service.application.rest;

import com.food.order.service.domain.dto.create.CreateOrderCommand;
import com.food.order.service.domain.dto.create.CreateOrderResponse;
import com.food.order.service.domain.dto.track.TrackOrderQuery;
import com.food.order.service.domain.dto.track.TrackOrderResponse;
import com.food.order.service.domain.ports.input.service.OrderApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/orders", produces = "application/vnd.api.v1+json")
public class OrderController {

    private final OrderApplicationService orderApplicationService;

    public OrderController(OrderApplicationService orderApplicationService) {
        this.orderApplicationService = orderApplicationService;
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody CreateOrderCommand createOrderCommand) {
        log.info("Creating order for customer id: {}", createOrderCommand.getCustomerId());
        var createOrderResponse = orderApplicationService.createOrder(createOrderCommand);
        log.info("Order created with tracking id: {}", createOrderResponse.getOrderTrackingId());

        return ResponseEntity.ok(createOrderResponse);
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<TrackOrderResponse> getOrderByTrackingId(@PathVariable UUID trackingId) {
        var response = orderApplicationService.trackOrder(
                TrackOrderQuery.builder().orderTrackingId(trackingId).build()
        );
        log.info("Returning order status with tracking id: {}", trackingId);
        return ResponseEntity.ok(response);
    }
}
