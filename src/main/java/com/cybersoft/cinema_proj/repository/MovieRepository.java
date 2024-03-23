package com.cybersoft.cinema_proj.repository;

import com.cybersoft.cinema_proj.entity.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<MovieEntity, Integer> , JpaSpecificationExecutor<MovieEntity> {
    MovieEntity findByName(String name);
    MovieEntity findById(int id);
    @Query("SELECT m FROM MovieEntity m WHERE m.name LIKE %:keyword%")
    List<MovieEntity> findByTenContainingKeyword(String keyword);

    @Query("SELECT m FROM MovieEntity m WHERE m.start_date <= :today")
    List<MovieEntity> findMoviesByStartDateBefore(@Param("today") Date today);

    @Query("SELECT m FROM MovieEntity m WHERE m.start_date > :today")
    List<MovieEntity> findMoviesByStartDateAfter(@Param("today") Date today);

    @Query("SELECT COUNT(m) FROM MovieEntity m")
    long countAllMovie();
}
