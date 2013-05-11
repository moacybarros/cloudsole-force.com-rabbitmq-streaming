package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/streaming")
public class TestStreamingController {

    @RequestMapping("")
    public String index() {
        return "streaming";
    }
}
