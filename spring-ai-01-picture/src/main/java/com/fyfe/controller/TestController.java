package com.fyfe.controller;

import com.fyfe.form.MyPictureForm;
import com.fyfe.service.TestService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pic")
public class TestController {
    @Resource
    private TestService testService;
    @PostMapping("/draw")

    public List<String> getPic(@RequestBody MyPictureForm form) {
        return testService.getPicture(form);
    }
 }
