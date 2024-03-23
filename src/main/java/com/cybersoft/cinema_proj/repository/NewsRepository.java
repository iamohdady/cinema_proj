package com.cybersoft.cinema_proj.repository;

import com.cybersoft.cinema_proj.entity.NewsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<NewsEntity, Integer> {
    NewsEntity findById(int id);

    @Query("SELECT n.image FROM NewsEntity n")
    List<String> findAllImages();
}
