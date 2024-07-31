package hello.itemservice.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
//@Table(name = "item") : class 이름과 같을 경우 생략
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * @Column을 생략할 경우 필드 이름을 테이블 컬럼 이름으로 사용한다.
     * 이 과정에서 객체 필드의 카멜 케이스를 스네이크 케이스로 자동 변환해준다.
     * 따라서 아래 코드의 @Column(name = "item_name")은 생략해도 된다.
      */
    @Column(name = "item_name", length = 10)
    private String itemName;
    private Integer price;
    private Integer quantity;

    // Jpa에서는 기본 생성자가 필수다.
    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
