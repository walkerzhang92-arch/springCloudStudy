package com.walker.courseservice.controller;

import com.walker.courseservice.common.ApiResult;
import com.walker.courseservice.dto.CourseDTO;
import com.walker.courseservice.service.CourseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/course")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/{courseId}")
    public ApiResult<CourseDTO> getCourse(@PathVariable Long courseId) {
        CourseDTO dto = courseService.getCourseById(courseId);
        return ApiResult.success(dto);
    }
}
