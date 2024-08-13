package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    /**
     * 머지 호출
     * 1. merge에 넘긴 파라미터에서 id를 통해 영속성 컨텍스트에서 1차로 find한다. 없다면 DB에서 가져온다.
     * 2. 엔티티를 찾았다면 merge에 넘긴 객체의 속성들로 전부 변경한다.(모든 속성 set)
     * 3. 변경된 엔티티를 DB에 저장한다.
     */
    public void save(Item item) {
        if(item.getId() == null) {
            em.persist(item);
        } else {
            em.merge(item); // 머지 호출
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
