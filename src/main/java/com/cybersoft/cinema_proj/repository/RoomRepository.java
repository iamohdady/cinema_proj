package com.cybersoft.cinema_proj.repository;

import com.cybersoft.cinema_proj.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Integer> {

    RoomEntity findById(Long id);

}
