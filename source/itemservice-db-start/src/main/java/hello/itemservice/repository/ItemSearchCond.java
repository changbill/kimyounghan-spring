package hello.itemservice.repository;

import lombok.Data;

/**
 * DTO
 * DTO지만 조건을 뜻하는 Condition의 줄임말인 Cond를 사용함으로써 데이터 전달 목적을 명확히함.
 * 따라서 DTO를 추가해 줄 필요없음
 */
@Data
public class ItemSearchCond {

    private String itemName;
    private Integer maxPrice;

    public ItemSearchCond() {
    }

    public ItemSearchCond(String itemName, Integer maxPrice) {
        this.itemName = itemName;
        this.maxPrice = maxPrice;
    }
}
