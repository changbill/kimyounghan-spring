package hello.itemservice.domain;

import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import hello.itemservice.repository.memory.MemoryItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Transactional
/**
 * @Transactional
 * 해당 어노테이션은 테스트에 있으면 트랜잭션 내에서 테스트를 실행하고,
 * 테스트가 끝나면 트랜잭션을 롤백시킨다.
 * 트랜잭션은 테스트에서 시작하므로 Service, Repository의 @Transactional도 테스트에서 시작한 트랜잭션에 참여한다.
 */
@SpringBootTest
/**
 * @SpringBootTest
 * main 폴더의 SpringBootApplication 어노테이션을 찾는다.
 * 그리고 SpringBootApplication의 설정을 가져온다.
  */
class ItemRepositoryTest {

    /**
     * 인터페이스를 테스트하자.
     * 인터페이스를 대상으로 하면 이후 구현체가 변경되었을 때에도 같은 테스트로 검증할 수 있다.
     */
    @Autowired
    ItemRepository itemRepository;

//    @Autowired
//    PlatformTransactionManager transactionManager;
//    TransactionStatus status;

//    @BeforeEach
//    void beforeEach() {
//        // 트랜잭션 시작
//        status = transactionManager.getTransaction(new DefaultTransactionDefinition());
//    }
    @AfterEach
    void afterEach() {
        //MemoryItemRepository 의 경우 제한적으로 사용
        // 실제 DB를 사용하는 경우 테스트가 끝난 후에 트랜잭션을 롤백하여 데이터를 초기화할 수 있다.
        if (itemRepository instanceof MemoryItemRepository) {
            ((MemoryItemRepository) itemRepository).clearStore();
        }
        // 트랜잭션 롤백
//        transactionManager.rollback(status);
    }

    /**
     * 메모리가 아닌 데이터베이스를 연결해서 테스트를 진행하면 기존의 데이터가 남아있어
     * 테스트 조건에 어긋나는 경우가 있다.
     * 이를 해결하기 위해 test전용 데이터베이스를 구분하자.
     */
    @Test
    void save() {
        //given
        Item item = new Item("itemA", 10000, 10);

        //when
        Item savedItem = itemRepository.save(item);

        //then
        Item findItem = itemRepository.findById(item.getId()).get();
        assertThat(findItem).isEqualTo(savedItem);
    }

    @Test
    void updateItem() {
        //given
        Item item = new Item("item1", 10000, 10);
        Item savedItem = itemRepository.save(item);
        Long itemId = savedItem.getId();

        //when
        ItemUpdateDto updateParam = new ItemUpdateDto("item2", 20000, 30);
        itemRepository.update(itemId, updateParam);

        //then
        Item findItem = itemRepository.findById(itemId).get();
        assertThat(findItem.getItemName()).isEqualTo(updateParam.getItemName());
        assertThat(findItem.getPrice()).isEqualTo(updateParam.getPrice());
        assertThat(findItem.getQuantity()).isEqualTo(updateParam.getQuantity());
    }

    /**
     * 데이터베이스에 영속적으로 저장되어 다시 테스트할 경우 영향을 줌
     * 이 문제를 해결하려면 각각의 테스트가 끝날 때마다 해당 테스트에서 추가한 데이터를 삭제해줘야한다.
     * 테스트 원칙
     * 1. 테스트는 다른 테스트와 격리해야한다.
     * 2. 테스트는 반복해서 실행할 수 있어야한다.
     * 이는 @Transactional 어노테이션으로 편리하게 적용할 수 있다.
     */
    @Test
    void findItems() {
        //given
        Item item1 = new Item("itemA-1", 10000, 10);
        Item item2 = new Item("itemA-2", 20000, 20);
        Item item3 = new Item("itemB-1", 30000, 30);

        log.info("repository={}", itemRepository.getClass());
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);

        //둘 다 없음 검증
        test(null, null, item1, item2, item3);
        test("", null, item1, item2, item3);

        //itemName 검증
        test("itemA", null, item1, item2);
        test("temA", null, item1, item2);
        test("itemB", null, item3);

        //maxPrice 검증
        test(null, 10000, item1);

        //둘 다 있음 검증
        test("itemA", 10000, item1);
    }

    void test(String itemName, Integer maxPrice, Item... items) {
        List<Item> result = itemRepository.findAll(new ItemSearchCond(itemName, maxPrice));
        assertThat(result).containsExactly(items);
    }
}
