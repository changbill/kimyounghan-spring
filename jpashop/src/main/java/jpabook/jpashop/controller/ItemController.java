package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    // model은 view에 넘기는 데이터
    @GetMapping("/items/new")
    public String createForm(Model model) {
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    @PostMapping("/items/new")
    public String create(BookForm form) {
        Book book = new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);
        return "redirect:/items";
    }

    @GetMapping("/items")
    public String list(Model model) {
        model.addAttribute("items", itemService.findItems());
        return "items/itemList";
    }

    /**
     * redirect 방식과 forward 방식의 차이(forward는 html name 앞에 아무것도 안적혀있는 경우)
     * redirect는 클라이언트가 보고있는 url을 이동시켜주는것
     * forward는 서버가 html 파일을 변경해 같은 url이어도 보는 화면을 달리하는 것
     */
    @GetMapping("items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {

        // Book이 아니라 Item을 넣어야하지만 쉬운 진행을 위해 Book으로만 한정지어 진행
        Book item = (Book) itemService.findOne(itemId);

        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

    /**
     * itemId를 요청 url에 받게되면 악의적인 요청이 들어올 경우를 생각해줘야함
     * 해당 itemId에 대한 권한이 있는지 검사하는 코드가 필요
     * session 객체는 요즘 잘 안쓰는 추세
     */
    @PostMapping("items/{itemId}/edit")
    public String updateItem(@PathVariable String itemId, @ModelAttribute("form") BookForm form) {
        /**
         * 병합
         * 준영속 상태의 엔티티를 영속 상태로 변경할 때 사용
         *
         * 준영속, 영속, 비영속 상태
         * 영속 상태는 엔티티가 영속성 컨텍스트 내에 존재하는 상태를 의미
         * 준영속 상태는 영속성 컨텍스트에 존재하다가
         *
         * 병합은 데이터 안정성에 좋지 않으므로 변경감지를 사용해야 한다.
         * 즉, 새로운 인스턴스에 Id를 넣어 merge해주면 안되고, id를 통해 find하여 update해줘야 한다.
         */
//        Book book = new Book();
//
//        book.setId(form.getId());
//        book.setName(form.getName());
//        book.setPrice(form.getPrice());
//        book.setStockQuantity(form.getStockQuantity());
//        book.setAuthor(form.getAuthor());
//        book.setIsbn(form.getIsbn());

//        itemService.saveItem(book);
        /**
         * 엔티티를 컨트롤러에서 생성하지 말것
         * 엔티티를 파라미터로 쓰지 말것
         */
        long id = Long.parseLong(itemId);
        itemService.updateItem(id, form.getName(), form.getPrice(), form.getStockQuantity());

        return "redirect:/items";
    }
}
