package com.joydada.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class indexController {

    @RequestMapping(value = {"/","/index.html"})
    public String index() {
        return "index";
    }
}
