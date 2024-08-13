package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    /**
     * 병합(merge)과 변경감지의 차이
     * 병합은 파라미터로 넘긴 객체의 id로 찾은 데이터를 객체의 속성으로 전부 변경한다.
     * 변경감지는 변경된 속성만 감지하여 그 부분만 update 쿼리를 통해 DB의 데이터를 변경한다.
     * 병합은 일부 속성이 null일 경우 그대로 변경해버리기 때문에 데이터의 안정성을 해칠 수 있다.
     *
     * @@@ 따라서 엔티티를 변경할 때는 항상 변경감지를 사용해야 한다!!! @@@
     */

    /**
     * 변경감지(dirty-checking)
     * 영속성 컨텍스트에 저장되어있는 변경사항들이 트랜잭션 커밋 시점에 JPA에 의해 변경감지되어
     * DB에 Update 쿼리를 실행
     */
    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity) {
        Item item = itemRepository.findOne(itemId);
        item.editItem(name, price, stockQuantity);   // update 메소드에서 setter 하나하나씩 만들지말고 별도의 메소드로 분리해야 함.
    }
    /* 파라미터가 많다 싶으면 서비스 계층에 DTO를 별도로 만들것.
    ex)
    public void updateItem(Long itemId, UpdateItemDto updateItemDto) {
        Item item = itemRepository.findOne(itemId);
        item.editItem(updateItemDto);
    }
    */

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }
}
