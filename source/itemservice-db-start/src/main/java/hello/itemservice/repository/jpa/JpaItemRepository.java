package hello.itemservice.repository.jpa;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
/**
 * @Repository
 * 1. 컴포넌트 스캔
 * 2. 예외 변환 AOP 적용
 *  스프링과 JPA를 함께 사용하는 경우 스프링은 JPA 예외 변환기를 등록한다.
 *  예외 변환 AOP 프록시는 JPA 관련 예외가 발생하면 스프링 데이터 접근 예외로 변환한다.
 */
@Transactional
public class JpaItemRepository implements ItemRepository {

    private final EntityManager em;

    public JpaItemRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Item save(Item item) {
        em.persist(item);
        return item;
    }

    /**
     * update 메소드
     * em.update같은 메소드가 없는데 어떻게 UPDATE SQL이 실행된 것일까?
     * JPA는 트랜잭션이 커밋되는 시점에 변경된 엔티티 객체가 있는지 확인한다.
     * 그리고 객체가 변경된 경우 UPDATE SQL을 실행한다.
     * 테스트의 경우에는 트랜잭션이 롤백되기 때문에 UPDATE SQL이 일어나지 않는다.
     * JPA가 어떻게 변경된 엔티티 객체를 확인하는지는 영속성 컨텍스트라는 내부 원리를 알아야한다.
     */
    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        Item findItem = em.find(Item.class, itemId);
        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());
    }

    @Override
    public Optional<Item> findById(Long id) {
        Item item = em.find(Item.class, id);
        return Optional.ofNullable(item);
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        /**
         * JPQL
         * 여러 데이터를 복잡한 조건으로 조회할 때 사용
         * SQL이 테이블을 대상으로 한다면, JPQL은 엔티티 객체를 대상으로 SQL을 실행한다.
         * 엔티티 객체와 속성의 이름은 앞글자가 대문자로 구분된다.
         *
         * 파라미터는 `:maxPrice`
         * 파라미터 바인딩은 `query.setParameter("maxPrice", maxPrice)`
         */
        String jpql = "select i from Item i";

        Integer maxPrice = cond.getMaxPrice();
        String itemName = cond.getItemName();

        if (StringUtils.hasText(itemName) || maxPrice != null) {
            jpql += " where";
        }

        boolean andFlag = false;
        List<Object> param = new ArrayList<>();
        if (StringUtils.hasText(itemName)) {
            jpql += " i.itemName like concat('%',:itemName,'%')";
            param.add(itemName);
            andFlag = true;
        }

        if (maxPrice != null) {
            if (andFlag) {
                jpql += " and";
            }
            jpql += " i.price <= :maxPrice";
            param.add(maxPrice);
        }

        log.info("jpql={}", jpql);

        TypedQuery<Item> query = em.createQuery(jpql, Item.class);
        if (StringUtils.hasText(itemName)) {
            query.setParameter("itemName", itemName);
        }
        if (maxPrice != null) {
            query.setParameter("maxPrice", maxPrice);
        }
        return query.getResultList();
    }
}
