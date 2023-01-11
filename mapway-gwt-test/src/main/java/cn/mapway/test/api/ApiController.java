package cn.mapway.test.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class ApiController {
    @RequestMapping(value ={"/","/index"} )
    public String index()
    {
        return "index";
    }
}
