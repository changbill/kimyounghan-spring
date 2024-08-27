package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.Result;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * X To One(ManyToOne or OneToOne) 성능 최적화
 * Order
 * Order -> Member (ManyToOne)
 * Order -> Delivery (OneToOne)
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    // ordersV1(엔티티를 바로 반환하는 방법)
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAll(new OrderSearch());
        for(Order order : all) {
            order.getMember().getName();    // Lazy 강제 초기화(프록시 객체로 있던 member 객체에 실제 값 조회해서 들어감)
            order.getDelivery().getAddress();   // Lazy 강제 초기화
        }
        return all;
    }

    /**
     * ordersV2(엔티티를 DTO로 변환해서 가져오는 방법)
     * 이 방식에도 문제점이 있으니 Order의 속성 Member, Delivery에서 불러올 때
     * Lazy가 초기화되어 조회 쿼리가 나간다.
     * 성능 최적화를 위해서 fetch join을 고려하자.
     */
    @GetMapping("/api/v2/simple-orders")
    public Result ordersV2() {
        List<SimpleOrderDto> collect = orderRepository.findAll(new OrderSearch()).stream()
                .map(SimpleOrderDto::new)
                .collect(toList());

        return new Result(collect);
    }

    /**
     * orderV3(패치 조인을 통해 쿼리 성능 최적화)
     * v2와 v3는 결과는 같으나 날리는 쿼리가 다르다.
     * fetch join으로 인해 N+1문제가 1개의 쿼리만 날리므로 성능 최적화를 이뤄낼 수 있다.
     */
    @GetMapping("/api/v3/simple-orders")
    public Result ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(toList());

        return new Result(result);
    }

    /**
     * ordersV4(DTO를 바로 DB에서 가져오는 방법)
     * repository에 하나의 API 만을 위한 SQL 쿼리를 짜서 DB 접근
     * 쿼리가 줄어든다는 장점이 있지만
     * API 재사용성 떨어짐.
     * 성능 최적화도 미미
     * API 스펙에 맞춘 코드가 Repository에 들어가는 단점
     * -> 만약 사용한다면 기존 Repository에서 별도로 분리하는걸 추천. 유지보수성이 좋아지기 때문
     *    view단에 의존적인 API인데
     */
    @GetMapping("/api/v4/simple-orders")
    public Result ordersV4() {
        return new Result(orderSimpleQueryRepository.findOrderDtos());
    }

    /**
     * DTO를 개별적으로 만들어야 하는 이유
     * Entity가 데이터에 드러나면 안됨. 이용자가 직접적인 데이터를 볼 수 있는 것은 좋지 않음.
     * api 조정이 들어갔을 때 다른 api에도 영향이 갈 수 있음.
     */
    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();     // Lazy 초기화(조회 쿼리 날아감)
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // Lazy 초기화
        }
    }
}
