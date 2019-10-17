package cn.shaines.filesystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author houyu
 * @createTime 2019/3/10 0:51
 */
@Controller
public class BaseController {

    @RequestMapping("/hello")
    @ResponseBody
    public String hello(){
        return "hello!";
    }

    @RequestMapping("")
    public String index(){
        return "redirect:/file/index";
    }

}
