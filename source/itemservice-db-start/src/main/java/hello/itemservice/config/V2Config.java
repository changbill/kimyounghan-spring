package hello.itemservice.config;

import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.jpa.JpaItemRepositoryV2;
import hello.itemservice.repository.jpa.JpaItemRepositoryV3;
import hello.itemservice.repository.jpa.SpringDataJpaItemRepository;
import hello.itemservice.repository.v2.ItemQueryRepositoryV2;
import hello.itemservice.repository.v2.ItemRepositoryV2;
import hello.itemservice.service.ItemService;
import hello.itemservice.service.ItemServiceV1;
import hello.itemservice.service.ItemServiceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@Configuration
@RequiredArgsConstructor
public class V2Config {

    private final EntityManager em;
    private final ItemRepositoryV2 itemRepositoryV2;    // Spring Data JPA

    /**
     * ItemService
     * Spring Data JPA(itemRepositoryV2)와 Querydsl(itemQueryRepositoryV2) 2가지에 의존관계를 형성하고 있다.
     * 여기서 Querydsl은 빈에 등록되어 있는 EntityManager를 가져다 쓰면서 인스턴스를 빈 등록해줬다.
     * Spring Data JPA는 JpaRepository를 상속받으며 자동으로 빈 등록이 되므로 등록된 인스턴스를 가져다썼다.
     */
    @Bean
    public ItemService itemService() {
        return new ItemServiceV2(itemRepositoryV2, itemQueryRepositoryV2());
    }

    @Bean
    public ItemQueryRepositoryV2 itemQueryRepositoryV2() {
        return new ItemQueryRepositoryV2(em);
    }

    /**
     * Spring Application은 위 2개 Bean 등록만으로 작동하나,
     * TestInit 메소드가 의존하고 있어 기존 Repository도 등록해준다.
     */
    @Bean
    public ItemRepository itemRepository() {
        return new JpaItemRepositoryV3(em);
    }
}
