package jpabook.jpashop.domain.item;

import jakarta.persistence.*;
import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")    // 싱글 테이블 전략으로 하나의 테이블에 타입을 나눔. 여기서는 추상클래스의 구현체로써 각 타입마다 속성을 달리했음
@Getter @Setter
public abstract class Item {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    //==비지니스 로직==//
    // 비지니스 로직은 service 뿐만 아니라 Entity 파일에도 작성할 수 있다.
    // 다만, 코드가 길어질 경우 가독성의 면에 문제가 생길 수 있고
    // Entity에서는 Repository를 불러오지 않으므로
    // Service와 Entity간의 비지니스 로직은 적절히 분배되어야 한다.
    /**
     * stock 증가
     */
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    /**
     * stock 감소
     */
    public void decreaseStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if(restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }
}
