package study.data_jpa.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.data_jpa.dto.MemberDto;
import study.data_jpa.entity.Member;
import study.data_jpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    /**
     * 도메인 클래스 컨버터
     * id(PK)를 통해 도메인 클래스를 가져오는 것
     * 권장 X, 조회용으로만 간단하게 쓸것
     */
    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    /**
     * Page, Pageable
     * http://localhost:8080/members?page=1&size=3&sort=id,desc&sort=username,desc
     * 이런식으로 URL에 조건을 달아서 결과를 반환받을 수 있다.
     * 반환 타입이 Page일 때 totalCount 쿼리가 나간다.
     *
     * 글로벌 pageable 기본값 설정 : yaml 파일에서 설정
     * @PageableDefault : API 단위로 pageable 기본값 설정
     *
     * 페이징 정보가 둘 이상이면 접두사로 구분
     * @Qualifier에 접두사명 추가 ex) /members?member_page=0&order_page=1
     * @Qualifier("member") Pageable memberPageable, @Qualifier("order") Pageable orderPageable
     */
    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 5, sort = "username") Pageable pageable) {
        return memberRepository.findAll(pageable)
                .map(m -> new MemberDto(m.getId(), m.getUsername(), null));
    }

    @PostConstruct
    public void init() {
        for (int i = 0; i < 100; i++) {
            memberRepository.save(new Member("user" + i, i));
        }
    }
}
