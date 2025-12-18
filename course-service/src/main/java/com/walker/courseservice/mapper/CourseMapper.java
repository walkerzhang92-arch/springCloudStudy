package com.walker.courseservice.mapper;

import com.walker.courseservice.entity.TCourse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CourseMapper {

    @Select("""
        SELECT
          id,
          title,
          price,
          stock,
          created_at AS createdAt
        FROM t_course
        WHERE id = #{id}
        """)
    TCourse selectById(@Param("id") Long id);
}
