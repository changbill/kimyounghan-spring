package study.data_jpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.data_jpa.entity.Item;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRepositoryTest {

    @Autowired ItemRepository itemRepository;

    /**
     * JpaRepository에서 구현되어 있는 save()는 새로운 엔티티면 'persist', 아니면 'merge'
     * 'merge'는 준영속 상태의 값을 영속화 할때 사용된다. DB를 호출해서 값을 확인하고 추가하는데,
     * 만약 DB에 값이 없다면 새로운 엔티티로 인식한다.
     *
     * PK 생성 전략이 @GeneratedValue 라면 save() 호출 시점에 식별자가 없으므로 새로운 엔티티로 인식해서 정상 작동한다.
     * 그런데 PK 생성 전략이 @Id만 사용하여 직접 할당이라면 이미 식별자 값이 있는 상태로 save()를 호출한다.
     * save()가 새로운 엔티티를 확인하는 전략은 객체가 null인지, 기본 타입이 0인지 확인하는 것이므로
     * 새로운 엔티티가 아니라고 판단하여 merge()가 호출된다.
     *
     * 이를 해결하기 위해서는 @GeneratedValue를 쓰는것이 가장 좋겠지만,
     * 'Persistable' 인터페이스를 Override하여 isNew(새로운 엔티티 확인 메소드)를 직접 구현하는 것이 차선이다.
     * @CreatedDate와 조합하여 새로운 엔티티 여부를 확인하면 효과적이다.
     */
    @Test
    public void save() {
        Item item = new Item(1L);
        itemRepository.save(item);
    }

}