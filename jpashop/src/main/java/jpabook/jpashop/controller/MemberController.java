package jpabook.jpashop.controller;

import jakarta.validation.Valid;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.naming.Binding;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    /**
     * Member 엔티티를 사용해도 되는데 form을 별도로 만든 이유?
     * 그대로 쓰게 된다면 member 엔티티와 form이 일치하지 않아
     * 추가 설정들이 필요할 수 있는데 이것들이 엔티티의 역할과 혼재될 가능성이 있다.
     */
    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result) {

        if(result.hasErrors()) {
            return "members/createMemberForm";
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/";
    }

    /**
     * Entity는 절대 외부(API)로 노출하지 말자
     * 내부에 있는 필드들이 보여질 뿐만 아니라
     * 엔티티에 로직을 추가할 때마다 API의 스펙이 변하기 때문에 프론트가 힘들어한다.
     * 그래서 API 개발의 경우 DTO를 별도로 만들어주는 것이 좋다.
     */
    @GetMapping("/members")
    public String list(Model model) {
        model.addAttribute("members", memberService.findMembers());
        return "members/memberList";
    }
}
