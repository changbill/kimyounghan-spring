package jpabook.jpashop.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

/**
 * 임베디드 객체로 만들었을 때 이점
 * 1. 코드 가독성이 좋아진다.
 * 단순히 city, street, zipcode라고 했을 때 엔티티의 컨셉과 맞지 않는다면 이해하기 어렵다.
 * 이를 하나로 통일시켜주는(엔티티의 컨셉과 일치하는) 임베디드 객체를 만들어 정리하면 깔끔하다.
 * 2. 하나의 컨셉으로 묶어주다보니 응집도가 높은 새 비지니스 로직을 추가하기 좋다.
 * 예를들어 아래의 Address에 city, street, zipcode 모두를 한번에 나열하는 fullAddress()라는 메소드를 만들 수 있다.
 * 3. 컬럼 길이와 같은 프로젝트 공통적으로 통일시켜야하는 속성의 경우 중복을 줄이고 실수를 줄일 수 있다.
 */
@Embeddable
public class Address {

    @Column(length = 10)
    private String city;
    @Column(length = 20)
    private String street;
    @Column(length = 5)
    private String zipcode;

    public String fullAddress() {
        return getCity() + " " + getStreet() + " " + getZipcode();
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getZipcode() {
        return zipcode;
    }

    private void setCity(String city) {
        this.city = city;
    }

    private void setStreet(String street) {
        this.street = street;
    }

    private void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    /**
     * getter를 통해 equals와 hashCode를 구현하도록 해야한다.
     * 이유는 프록시일 때에도 진짜 객체에 가도록 할 수 있기 때문이다.
     * getter를 사용하지 않는다면 프록시 객체의 속성들을 가져올 수 있다.
      */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(getCity(), address.getCity()) && Objects.equals(getStreet(), address.getStreet()) && Objects.equals(getZipcode(), address.getZipcode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCity(), getStreet(), getZipcode());
    }
}
