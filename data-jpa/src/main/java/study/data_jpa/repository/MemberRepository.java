package study.data_jpa.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.data_jpa.dto.MemberDto;
import study.data_jpa.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    // 파라미터 바인딩
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    // 값 반환
    @Query("select m.username from Member m")
    List<String> findUsernameList();

    // DTO 반환
    @Query("select new study.data_jpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    // 컬렉션 쿼리 적용
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    List<Member> findListByUsername(String username);   // 컬렉션에서 조건에 부합하는 데이터가 없을 경우 빈 컬렉션 반환
    Member findMemberByUsername(String username);
    Optional<Member> findOptionalByUsername(String username);

    /**
     * Page를 쓸 때 Join을 한다면 count 쿼리에서 조인이 필요가 없어도 함께 조인되는 경우가 있음
     * 성능 최적화를 위해 countQuery를 별도로 작성해주는 방법이 있다. 전체 count 쿼리는 매우 무겁다.
      */
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

//    Slice<Member> findByAge(int age, PageRequest pageRequest);

    /**
     * 벌크성 쿼리의 특성 상 영속성 컨텍스트를 바로 거치지 않고(select 절 생략) 직접 DB에 쿼리를 날리게 된다.
     * 벌크성 쿼리(update)는 대량으로 수정해야 하는 쿼리를 날릴 때, 일반적인 변경감지로는 성능이 나오지 않아 사용한다.
     * 수정 쿼리 넣을 때는 @Modifying 넣어줘야함
     * clearAutomatically true는 벌크연산에서 flush와 clear를 자동으로 해줘 결과가 영속성 컨텍스트에 저장된다.
     */
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberByFetchJoin();

    /**
     * EntityGraph
     * 패치 조인을 JPQL 없이 간단히 적용가능한 기능
     * @EntityGraph(attributePaths = {"name"})
     * 사용은 이렇게 하고 "name" 위치에 원하는 패치 조인 대상을 적어주면 된다.
     * 간단한 패치조인은 EntityGraph를 활용하고 복잡한 로직의 경우 JPQL을 활용해주면 된다.
     */
//    @Override
//    @EntityGraph(attributePaths = {"team"})
//    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    // 참고로 find...BySth에서 find와 By 사이에는 아무거나 들어가도 상관없다.
    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    /**
     * QueryHint
     * readOnly는 hibernate에서 밖에 지원을 안하는데 JPA에서도 사용할 수 있게하는 기능
     * 이렇게 QueryHint를 사용했을 때, EntityManager에서 변경감지하기 위해서 기존의 데이터 스냅샷 저장을 안 한다.
     */
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    // TODO : Lock에 대해서 추가적으로 알아보기(optimistic lock, pessimistic lock 등)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);

    @Query(value = "select * from member where username = ?", nativeQuery = true)
    Member findByNativeQuery(String username);

    @Query(value = "select m.member_id as id, m.username, t.name as teamName " +
                    "from member m left join team t",
                    countQuery = "select count(*) from member",
                    nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);
}
