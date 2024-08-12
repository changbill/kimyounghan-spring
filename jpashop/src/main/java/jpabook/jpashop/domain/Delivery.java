package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Delivery {

    @Id @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)    // mappedBy는 거울, 보기만 하는 것
    private Order order;

    @Embedded
    private Address address;

    /**
     * Enum Type @Enumerated 어노테이션 꼭 붙일 것!!
     * 기본값 : EnumType.ORDINAL
     * 0, 1, 2 이런식으로 숫자로 들어가게 되는데 이 경우 Enum파일에 새로운 Enum이 추가되었을 경우
     * READY(0), COMP(1) 에서 READY(0), WAIT(1), COMP(2) 이런식으로 숫자가 밀리게 됨
     * 따라서 EnumType.STRING으로 할 것!!
      */
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;  // Enum [READY, COMP(배송)]
}
