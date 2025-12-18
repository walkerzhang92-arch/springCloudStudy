package com.walker.orderservice.feign;

import com.walker.orderservice.common.ApiResult;
import com.walker.orderservice.dto.CourseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "course-service")
public interface CourseFeignClient {

    @GetMapping("/internal/course/{courseId}")
    ApiResult<CourseDTO> getCourse(@PathVariable("courseId") Long courseId);
}
