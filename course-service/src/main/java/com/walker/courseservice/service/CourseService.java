package com.walker.courseservice.service;

import com.walker.courseservice.dto.CourseDTO;

public interface CourseService {
    CourseDTO getCourseById(Long courseId);
}
