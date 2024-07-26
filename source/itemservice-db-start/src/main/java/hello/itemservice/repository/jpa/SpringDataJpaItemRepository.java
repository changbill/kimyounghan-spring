package hello.itemservice.repository.jpa;

import hello.itemservice.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data Jpa Interface
 * extends 이후 들어가는 JpaRepository에 <엔티티명, id 타입>이 들어가야 한다.
 * JpaRepository를 상속받으면 스프링 데이터 Jpa 프록시 Repository를 생성해준다.
 * 이를 통해 간단한 쿼리 메소드가 구현된다.
 */
public interface SpringDataJpaItemRepository extends JpaRepository<Item, Long> {

    // findAll()의 경우 공통 인터페이스에서 제공하는 기능
    List<Item> findByItemNameLike(String itemName);
    List<Item> findByPriceLessThanEqual(Integer price);

    // 쿼리 메소드
    List<Item> findByItemNameLikeAndPriceLessThanEqual(String itemName, Integer price);

    // 쿼리 직접 실행(JPQL)
    @Query("select i from Item i where i.itemName like :itemName and i.price <= :price")
    List<Item> findItems(@Param("itemName") String itemName, @Param("price") Integer price);

}
