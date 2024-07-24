package hello.hello_spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class HelloController {

    @GetMapping("hello")    // localhost:8080/hello 라는 url로 요청이 들어오면 여기로 연결
    public String hello(Model model) {
        model.addAttribute("data", "spring!!");    // Model에 data: hello! String형으로 넣기
        return "hello";     // viewResolver가 resources:templetes/{ViewName(지금의 경우 hello)}.html 찾아준다
    }

    @GetMapping("hello-mvc")
    // url로 넘어오는 값 model로 넘기기
    // RequestParam의 required 값은 true가 기본값이므로 안넣어주면 에러를 일으킨다.
    // http://localhost:8080/hello-mvc?name=spring!!! 이런식으로 넣어줘야한다
    public String helloMvc(@RequestParam("name") String name, Model model) {
        model.addAttribute("name", name);
        return "hello-template";
    }

    @GetMapping("hello-string")
    @ResponseBody
    // 기존의 template 방식은 html 파일에서 일부 수정을 거치는 과정이라면
    // ResponseBody 어노테이션은 http의 body부에 직접 보내는 것
    // viewResolver 대신에 HttpMessageConverter가 동작
    public String helloString(@RequestParam("name") String name) {
        return "hello" + name;
    }

    @GetMapping("hello-api")
    @ResponseBody
    public Hello helloApi(@RequestParam("name") String name) {
        Hello hello = new Hello();
        hello.setName(name);
        return hello;
    }

    static class Hello {
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        private String name;

        public Hello() {
        }


    }
}
