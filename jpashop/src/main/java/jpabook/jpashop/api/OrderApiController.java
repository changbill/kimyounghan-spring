package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.Result;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    /**
     * ordersV1 엔티티를 그대로 반환
     * 조회하고 싶은 order의 속성 Member와 Delivery, orderItems를
     * 영속성 엔티티에 저장할 수 있도록 (해당 엔티티의 속성을)조회하는 코드를 추가.
     */
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAll(new OrderSearch());
        for(Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return all;
    }

    /**
     * ordersV2 엔티티를 반환
     * 문제점. 엔티티를 조회할때마다 lazy가 적용되어 N+1문제가 터짐.
     * 성능 최적화를 위해 fetch join을 고려해봐야 함.
     */
    @GetMapping("/api/v2/orders")
    public Result ordersV2() {

        List<Order> orders = orderRepository.findAll(new OrderSearch());
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return new Result(collect);
    }

    /**
     * ordersV3 fetch join을 통해 성능 최적화
     * 1대다 패치 조인(컬렉션 패치 조인) 시 페이징 쿼리 불가.
     * 모든 데이터를 DB에서 읽어오고 메모리에서 페이징 해버린다(OutOfMemory 오류가 날 수 있음).
     * 그리고 컬렉션 패치 조인은 하나만 써야한다. 두개 이상 써버리면 1 * N * M의 데이터가 불러와질 수 있다
     */
    @GetMapping("/api/v3/orders")
    public Result ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();

        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return new Result(result);
    }

    /**
     * ordersV3_page 페이징 처리를 위해 fetch join 대신 batch size 조정
     * batch size는 yaml 파일에 default_batch_fetch_size 설정을 통해 글로벌하게 지정 가능.
     * batch size를 지정하면 해당 개수만큼 in (?, ?, ? ...) 쿼리를 날려 한번에 여러개를 가져올 수 있다.
     * 따라서 V3에서의 1*N*M 개의 쿼리 호출 로직이 필요한 1대다 속성 개수만큼 쿼리를 호출하게 된다.
     * 다만, xToOne 관계의 경우 기존의 fetch join을 통해 쿼리를 줄일 수 있으므로 그대로 사용한다.
     */
    @GetMapping("/api/v3.1/orders")
    public Result ordersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit
    ) {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);

        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return new Result(result);
    }

    /**
     * ordersV4 DB 데이터 DTO로 받아오는 API
     * 1대다를 Id 하나당 쿼리 하나씩 호출하니 N+1문제가 생긴다.
     */
    @GetMapping("/api/v4/orders")
    public Result ordersV4() {
        List<OrderQueryDto> orderQueryDtos = orderQueryRepository.findOrderQueryDtos();

        return new Result(orderQueryDtos);
    }

    /**
     * ordersV5
     * Id를 컬렉션으로 받아와 IN 쿼리를 사용.
     */
    @GetMapping("/api/v5/orders")
    public Result ordersV5() {
        List<OrderQueryDto> orderQueryDtos = orderQueryRepository.findAllByDto_optimization();

        return new Result(orderQueryDtos);
    }

    @GetMapping("/api/v6/orders")
    public Result ordersV6() {
        List<OrderFlatDto> orderFlatDtos = orderQueryRepository.findAllByDto_flat();

        return new Result(orderFlatDtos);
    }


    @Data
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        /**
         * DTO로 변환해서 반환하라는 것이 단순히 DTO로 데이터를 보내면 끝이 아니다.
         * 아래의 OrderDto에는 orderItems라는 엔티티가 들어있다.
         * 이를 그대로 보낸다면 결국 엔티티를 내보내게 되는것이다. 이또한 DTO로 변경해주어야 한다.
         */
        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(Collectors.toList());
        }

    }

    @Data
    static class OrderItemDto {

        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
}
