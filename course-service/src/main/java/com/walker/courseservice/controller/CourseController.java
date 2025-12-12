package com.walker.courseservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CourseController {

    @GetMapping("/course/{id}")
    public String getCourse(@PathVariable Long id) {
        return "Course-" + id;
    }
}
