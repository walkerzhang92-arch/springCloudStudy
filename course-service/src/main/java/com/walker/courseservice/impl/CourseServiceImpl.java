package com.walker.courseservice.impl;

import com.walker.courseservice.dto.CourseDTO;
import com.walker.courseservice.entity.TCourse;
import com.walker.courseservice.mapper.CourseMapper;
import com.walker.courseservice.service.CourseService;
import org.springframework.stereotype.Service;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseMapper courseMapper;

    public CourseServiceImpl(CourseMapper courseMapper) {
        this.courseMapper = courseMapper;
    }

    @Override
    public CourseDTO getCourseById(Long courseId) {
        if (courseId == null || courseId <= 0) {
            throw new IllegalArgumentException("courseId 非法");
        }

        TCourse course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new RuntimeException("课程不存在: " + courseId);
        }

        CourseDTO dto = new CourseDTO();
        dto.setId(course.getId());
        dto.setTitle(course.getTitle());
        dto.setPrice(course.getPrice());
        dto.setStock(course.getStock());
        return dto;
    }
}
