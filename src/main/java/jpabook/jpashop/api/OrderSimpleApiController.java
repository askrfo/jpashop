package jpabook.jpashop.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

  private final OrderRepository orderRepository;
  private final OrderSimpleQueryRepository orderSimpleQueryRepository;

  @GetMapping(value = "/api/v1/simple-orders")
  public List<Order> orderV1() {
    List<Order> all = orderRepository.findAllByString(new OrderSearch());
    for (Order order : all) {
      order.getMember().getName();  //Lazy 강제 초기화
      order.getDelivery().getAddress(); //Lazy 강제 초기화
      //order.getOrderItems().stream().forEach(o -> o.getItem().getName());
    }
    return all;
  }

  @GetMapping(value = "/api/v2/simple-orders")
  public List<OrderSimpleDto> orderV2() {
    //주문 2개
    //N + 1 -> 1 + 회원 N + 배송 N

    List<Order> orders = orderRepository.findAllByString(new OrderSearch());
    List<OrderSimpleDto> result = orders.stream().map(o -> new OrderSimpleDto(o))
        .collect(Collectors.toList());

    return result;
  }

  @GetMapping(value = "/api/v3/simple-orders")
  public List<OrderSimpleDto> orderV3() {
    List<Order> orders = orderRepository.findAllWithMemberDelievery();
    List<OrderSimpleDto> result = orders.stream().map(o -> new OrderSimpleDto(o))
        .collect(Collectors.toList());
    return result;
  }

  @GetMapping(value = "/api/v4/simple-orders")
  public List<OrderSimpleQueryDto> orderV4() {
    return orderSimpleQueryRepository.findOrderDtos();
  }

  @Data
  static class OrderSimpleDto{
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;

    public OrderSimpleDto(Order order) {
      this.orderId = order.getId();
      this.name = order.getMember().getName();
      this.orderDate = order.getOrderDate();
      this.orderStatus = order.getStatus();
      this.address = order.getDelivery().getAddress();
    }
  }
}
