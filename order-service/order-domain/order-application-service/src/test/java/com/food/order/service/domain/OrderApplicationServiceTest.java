package com.food.order.service.domain;

import com.food.domain.valueobject.*;
import com.food.order.service.domain.dto.create.CreateOrderCommand;
import com.food.order.service.domain.dto.create.OrderAddress;
import com.food.order.service.domain.dto.create.OrderItem;
import com.food.order.service.domain.entity.Customer;
import com.food.order.service.domain.entity.Order;
import com.food.order.service.domain.entity.Product;
import com.food.order.service.domain.entity.Restaurant;
import com.food.order.service.domain.exception.OrderDomainException;
import com.food.order.service.domain.mapper.OrderDataMapper;
import com.food.order.service.domain.ports.input.service.OrderApplicationService;
import com.food.order.service.domain.ports.output.repository.CustomerRepository;
import com.food.order.service.domain.ports.output.repository.OrderRepository;
import com.food.order.service.domain.ports.output.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// so that the mocked classed be accessible here
@SpringBootTest(classes = OrderTestConfiguration.class)
// to create one instance of this class. default foreach
// test method a new instance of this class is created.
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderApplicationServiceTest {

    @Autowired
    private OrderApplicationService orderApplicationService;

    @Autowired
    private OrderDataMapper orderDataMapper;

    // this will be wired from the mock in the OrderTestConfiguration class
    @Autowired
    private OrderRepository orderRepository;

    // this will be wired from the mock in the OrderTestConfiguration class
    @Autowired
    private CustomerRepository customerRepository;

    // this will be wired from the mock in the OrderTestConfiguration class
    @Autowired
    private RestaurantRepository restaurantRepository;

    private CreateOrderCommand createOrderCommand;
    private CreateOrderCommand createOrderCommandWrongPrice;
    private CreateOrderCommand createOrderCommandWrongProductPrice;
    private final UUID CUSTOMER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final UUID RESTAURANT_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private final UUID PRODUCT_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private final UUID ORDER_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private final BigDecimal PRICE = BigDecimal.valueOf(200);

    @BeforeAll
    public void init() {
        createOrderCommand = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .address(OrderAddress.builder()
                        .street("street_1")
                        .postalCode("1000AB")
                        .city("Paris")
                        .build())
                .price(PRICE)
                .items(List.of(OrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(1)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("50.00"))
                                .build(),
                        OrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(3)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("150.00"))
                                .build()))
                .build();

        createOrderCommandWrongPrice = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .address(OrderAddress.builder()
                        .street("street_1")
                        .postalCode("1000AB")
                        .city("Paris")
                        .build())
                .price(new BigDecimal("250.00"))
                .items(List.of(OrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(1)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("50.00"))
                                .build(),
                        OrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(3)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("150.00"))
                                .build()))
                .build();

        createOrderCommandWrongProductPrice = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .address(OrderAddress.builder()
                        .street("street_1")
                        .postalCode("1000AB")
                        .city("Paris")
                        .build())
                .price(new BigDecimal("210.00"))
                .items(List.of(OrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(1)
                                .price(new BigDecimal("60.00"))
                                .subTotal(new BigDecimal("60.00"))
                                .build(),
                        OrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(3)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("150.00"))
                                .build()))
                .build();

        Customer customer = new Customer(new CustomerId(CUSTOMER_ID));

        Restaurant restaurantResponse = Restaurant.builder()
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .products(List.of(
                        new Product(new ProductId(PRODUCT_ID), "product-1", new Money(new BigDecimal("50.00"))),
                        new Product(new ProductId(PRODUCT_ID), "product-2", new Money(new BigDecimal("50.00")))))
                .active(true)
                .build();

        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        order.setId(new OrderId(ORDER_ID));

        when(customerRepository.findCustomer(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(restaurantRepository.findRestaurantInformation(orderDataMapper.createOrderCommandToRestaurant(createOrderCommand))).thenReturn(Optional.of(restaurantResponse));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

    }

    @Test
    public void testCreateOrder() {
        var createOrderResponse = orderApplicationService.createOrder(createOrderCommand);
        assertEquals(OrderStatus.PENDING, createOrderResponse.getOrderStatus());
        assertEquals("Order created successfully", createOrderResponse.getMessage());
        assertNotNull(createOrderResponse.getOrderTrackingId());
    }

    @Test
    public void testCreateOrderWrongPrice() {
        var orderDomainException = assertThrows(OrderDomainException.class,
                () -> orderApplicationService.createOrder(createOrderCommandWrongPrice));

        assertEquals("Total price: 200.00 is not equal to order price: 250.00",
                orderDomainException.getMessage());
    }

    @Test
    public void testCreateOrderWithWrongProductPrice() {
        var orderDomainException = assertThrows(OrderDomainException.class,
                () -> orderApplicationService.createOrder(createOrderCommandWrongProductPrice));
        assertEquals("Order item price: 60.00 is not valid for product: " + PRODUCT_ID,
                orderDomainException.getMessage());
    }

    @Test
    public void testCreateOrderWithPassiveRestaurant() {
        var restaurantRes = Restaurant.builder()
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .products(List.of(
                        new Product(new ProductId(PRODUCT_ID), "product-1", new Money(new BigDecimal("50.00"))),
                        new Product(new ProductId(PRODUCT_ID), "product-2", new Money(new BigDecimal("50.00")))))
                .active(false)
                .build();
        when(restaurantRepository.findRestaurantInformation(orderDataMapper.createOrderCommandToRestaurant(createOrderCommand)))
                .thenReturn(Optional.of(restaurantRes));

        var orderDomainException = assertThrows(OrderDomainException.class,
                () -> orderApplicationService.createOrder(createOrderCommand));

        assertEquals("Restaurant with id: "+ RESTAURANT_ID +" is not active",
                orderDomainException.getMessage());
    }

}
