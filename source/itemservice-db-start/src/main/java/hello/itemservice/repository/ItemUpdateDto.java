package hello.itemservice.repository;

import lombok.Data;

/**
 * DTO : 데이터 전송 객체
 * DTO는 기능은 없고 데이터를 전달만 하는 용도로 사용되는 객체를 뜻한다.
 * 참고로 DTO에 기능이 있으면 안되는가? 그것은 아니다. 객체의 주 목적이 데이터를 전송하는 것이라면
 * DTO라 할 수 있다.
 * 객체 이름에 DTO를 꼭 붙여야 하는 것은 아니다. 대신 붙여두면 용도를 알 수 있다는 장점은 있다.
 */
@Data
public class ItemUpdateDto {
    private String itemName;
    private Integer price;
    private Integer quantity;

    public ItemUpdateDto() {
    }

    public ItemUpdateDto(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
