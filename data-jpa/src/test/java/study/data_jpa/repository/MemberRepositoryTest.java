package study.data_jpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import study.data_jpa.dto.MemberDto;
import study.data_jpa.entity.Member;
import study.data_jpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember() throws Exception {
        // given
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        // when
        Member findMember = memberRepository.findById(member.getId()).get();

        // then
        assertThat(findMember.getId()).isEqualTo(savedMember.getId());
        assertThat(findMember.getUsername()).isEqualTo(savedMember.getUsername());
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);
        List<Member> result =
                memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testQuery() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(member1);
    }

    @Test
    public void findUsernameList() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<String> usernameList = memberRepository.findUsernameList();
        assertThat(usernameList.size()).isEqualTo(2);
    }

    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        memberRepository.save(m1);
        m1.changeTeam(team);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        assertThat(memberDto.get(0).getTeamName()).isEqualTo(team.getName());
        assertThat(memberDto.get(0).getUsername()).isEqualTo(m1.getUsername());
    }

    @Test
    public void findByNames() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> findByNames = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        assertThat(findByNames.size()).isEqualTo(2);
        assertThat(findByNames.stream().filter(e -> e.getUsername().equals("AAA") || e.getUsername().equals("BBB")).collect(Collectors.toList())).size().isEqualTo(2);
    }

    @Test
    public void returnType() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> aaa = memberRepository.findListByUsername("AAA");

    }

    @Test
    public void paging() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));
        memberRepository.save(new Member("member6", 10));

        // when
        int age = 10;
        /**
         * 0 번 페이지부터 3개씩, 정렬 내림차순 username을 기준으로
         * page 인덱스는 0부터 시작
          */
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        /**
         * page : page + count 쿼리
         * slice : page(내부적으로 limit+1 조회)
         * List : page
         * 전체 count 쿼리는 매우 무거우므로 유용하게 쓰자.
         */
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        /**
         * Page<Member> 이런식으로 Controller에서 API 반환하면 안된다. 엔티티이기 때문에 DTO로 변환해서 반환해야 한다.
         * 이 경우 page.map을 사용한다.
         */
        Page<MemberDto> toMap = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));
//        Slice<Member> page = memberRepository.findByAge(age, pageRequest);
//        List<Member> page = memberRepository.findByAge(age, pageRequest);

        // then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        assertThat(content.size()).isEqualTo(3);        // 첫번째 컨텐츠 수
        assertThat(totalElements).isEqualTo(6);         // 총 원소 수
        assertThat(page.getNumber()).isEqualTo(0);      // 현재 페이지 몇번째인지
        assertThat(page.getTotalPages()).isEqualTo(2);  // 총 페이지 수
        assertThat(page.isFirst()).isTrue();        // 첫번째 페이지인지
        assertThat(page.hasNext()).isTrue();        // 다음 페이지가 있는지

    }

    /**
     * 벌크 연산을 했을 시에 영속성 컨텍스트에 저장되지 않으므로 수정 이전 데이터가 반환된다.
     * EntityManager를 통해 flush, clear를 해준 후 데이터를 새롭게 받는다.
     * API가 update만 끝내면 문제 없다. 하지만 다른 로직이 함께 돌아가면 생각해줘야 하는 부분이다.
     */
    @Test
    public void bulkUpdate() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 21));
        memberRepository.save(new Member("member3", 23));
        memberRepository.save(new Member("member4", 24));
        memberRepository.save(new Member("member5", 31));

        int resultCount = memberRepository.bulkAgePlus(20);
//        em.flush();
//        em.clear();

        Member member5 = memberRepository.findMemberByUsername("member5" );
        System.out.println("member5 = " + member5);

        assertThat(resultCount).isEqualTo(4);
    }

    @Test
    public void findMemberLazy() {
        // given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // when
        List<Member> all = memberRepository.findMemberByFetchJoin();
        for (Member member : all) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }

    @Test
    public void queryHint() {
        memberRepository.save(new Member("member1", 10));
        em.flush(); // 영속성 컨텍스트에 있는 데이터를 db에 동기화
        em.clear(); // 영속성 컨텍스트에 남아있는 데이터를 삭제

        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.changeUsername("member2");

        em.flush();
    }

    @Test
    public void lock() {
        memberRepository.save(new Member("member1", 10));
        em.flush(); // 영속성 컨텍스트에 있는 데이터를 db에 동기화
        em.clear(); // 영속성 컨텍스트에 남아있는 데이터를 삭제

        List<Member> result = memberRepository.findLockByUsername("member1" );
    }

    @Test
    public void callCustom() {
        List<Member> result = memberRepository.findMemberCustom();
    }

    /**
     * 데이터 JPA에서 네이티브 쿼리 사용하는 방법
     * 쿼리 결과(데이터)를 DTO로 반환받을 때
     * 1. 데이터 JPA 사용
     * 2. JPQL 사용
     * 3. 네이티브 SQL 사용
     * 하지만 데이터 JPA에서 지원하는 네이티브 쿼리가 아닌 JdbcTemplate이나 MyBatis 권장
     * 일부 반환타입 지원 안함 등 불완전한 부분이 많기 때문.
     */
    @Test
    public void nativeQuery() throws Exception {
        // given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamA);
        em.persist(member1);
        em.persist(member2);

        em.flush();
        em.clear();

        // when
        Page<MemberProjection> result = memberRepository.findByNativeProjection(PageRequest.of(1, 10));
        List<MemberProjection> content = result.getContent();
        for (MemberProjection memberProjection : content) {
            System.out.println("memberProjection = " + memberProjection.getUsername());
            System.out.println("memberProjection = " + memberProjection.getTeamName());
        }
        // then

    }
}