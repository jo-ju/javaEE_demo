package controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.time.LocalDate;

@Controller
public class UserController {
    @RequestMapping(value = "/hello", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String hello() {
        return "黎俊驹 2513202060211  "
                + LocalDate.now().toString();
    }
}
