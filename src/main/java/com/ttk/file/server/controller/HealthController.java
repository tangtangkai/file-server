package com.ttk.file.server.controller;

import com.ttk.file.server.config.FileConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @Autowired
    private FileConfig fileConfig;

    @GetMapping("/health/{value}")
    public Object getConfig(@PathVariable String value){
        return fileConfig.getFile().get(value);
    }
}
