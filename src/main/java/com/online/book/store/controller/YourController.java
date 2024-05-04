package com.online.book.store.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import datadog.trace.api.Trace;

@RestController
@RequestMapping("/api")
public class YourController {

    @GetMapping("/hello")
    @Trace(operationName = "hello.request")
    public int hello(){
        return 5;
    }
    
}
