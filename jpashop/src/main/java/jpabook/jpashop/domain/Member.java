package jpabook.jpashop.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    private Long id;

    @NotEmpty
    private String name;

    @Embedded
    private Address address;

    // Order 필드에 있는 member에 의해 맵핑 된 것이라는 뜻
    // 읽기 전용이 되어 버림
    // 여기서 어떤 값을 넣는다고 foreign key 값이 변경되지 않음
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();
}
