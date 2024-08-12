package jpabook.jpashop.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class Address {

    private String city;
    private String street;
    private String zipcode;

    /**
     * JPA를 사용하려면 기본 생성자가 필요하다.(reflection 기능으로 인해)
     * JPA에서는 protected 접근제어자까지 지원한다.
     *
     * 값 타입은 변경 불가하게 설계해야한다는 원칙에 따라
     * setter를 제거하고 생성자로만 이 객체를 만들 수 있게 설계한 상태이다.
     * 따라서 다른 곳에서 기본 생성자를 상속받지 못하도록 protected를 사용해놓았다.
     */
    protected Address() {
    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
