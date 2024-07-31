package hello.itemservice;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@Slf4j
@RequiredArgsConstructor
public class TestDataInit {

    /**
     * 디폴트로 아래와 같이 타입을 기준으로 등록된 빈을 사용하지만
     * private final ItemRepository itemRepository;
     *
     * @Autowired
     * @Qualifier("itemRepository")
     * private ItemRepository itemRepository;
     * 이와 같이 나타내면 등록된 빈의 메소드 이름으로 빈 인스턴스를 사용할 수 있다.
     */
    private final ItemRepository itemRepository;

    /**
     * 확인용 초기 데이터 추가
     * EventListener(ApplicationReadyEvent.class)는 스프링 컨테이너가 완전히 초기화를 끝내고,
     * 실행 준비가 되었을 때 발생하는 이벤트다. 스프링은 이 시점에 initData() 메소드를 호출한다.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initData() {
        log.info("test data init");
        itemRepository.save(new Item("itemA", 10000, 10));
        itemRepository.save(new Item("itemB", 20000, 20));
    }

}
