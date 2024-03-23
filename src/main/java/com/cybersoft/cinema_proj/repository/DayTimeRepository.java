package com.cybersoft.cinema_proj.repository;

import com.cybersoft.cinema_proj.entity.DayTimeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DayTimeRepository extends JpaRepository<DayTimeEntity, Integer> {
}
