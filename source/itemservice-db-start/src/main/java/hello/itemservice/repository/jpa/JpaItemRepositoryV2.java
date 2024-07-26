package hello.itemservice.repository.jpa;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
@RequiredArgsConstructor
public class JpaItemRepositoryV2 implements ItemRepository {

    /**
     * SpringDataJpaItemRepository
     * 스프링 애플리케이션 구동 시 Spring Boot와 Spring Data Jpa가
     * 인터페이스 스펙을 보고 구현체를 만들어 스프링 빈으로 등록한다.
     * JpaItemRepositoryV2도 빈으로 등록될 때 SpringDataJpaItemRepository의 구현체를 DI 받는다.
     *
     * 즉, 인터페이스 타입의 생성자를 받고 있지만 런타임 시 인스턴스는 구현체가 들어오므로 기능을  사용할 수 있다.
     */
    private final SpringDataJpaItemRepository repository;

    @Override
    public Item save(Item item) {
        // SpringDataJpaItemRepository의 상위 인터페이스를 올라가다보면 CrudRepository에서 save를 구현하고 있다
        return repository.save(item);
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        Item findItem = repository.findById(itemId).orElseThrow();
        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());
    }

    @Override
    public Optional<Item> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

        if(StringUtils.hasText(itemName) && maxPrice != null) {
            return repository.findItems("%" + itemName + "%", maxPrice);
        } else if(StringUtils.hasText(itemName)) {
            return repository.findByItemNameLike("%" + itemName + "%");
        } else if(maxPrice != null) {
            return repository.findByPriceLessThanEqual(maxPrice);
        } else {
            return repository.findAll();
        }
    }
}
