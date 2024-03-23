package com.cybersoft.cinema_proj.specification;

import com.cybersoft.cinema_proj.entity.MovieEntity;
import org.springframework.data.jpa.domain.Specification;

public class MovieSpecifications {

    public static Specification<MovieEntity> hasGenre(String genre) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("theLoai"), "%" + genre + "%");
    }
}
