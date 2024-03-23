package com.cybersoft.cinema_proj.repository;

import com.cybersoft.cinema_proj.dto.MemberDTO;
import com.cybersoft.cinema_proj.entity.BillEntity;
import com.cybersoft.cinema_proj.entity.MemberEntity;
import com.cybersoft.cinema_proj.entity.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Integer> {

    MemberEntity findByUsername(String username);

    MemberEntity findById(int id);

    List<MemberEntity> findAll();

    @Query("SELECT tv " +
        "FROM MemberEntity tv " +
        "WHERE tv.username = :username")
    MemberEntity findMemberWithCustomerByUsername(@Param("username") String username);

    @Query("SELECT COUNT(m) FROM MemberEntity m")
    long countAllMembers();

    @Query("SELECT m FROM MemberEntity m WHERE m.status_payment = false")
    List<MemberEntity> findMembersWithUnconfirmedPayments();

    @Query("SELECT m.role FROM MemberEntity m WHERE m.username = :username")
    String findRoleByUsername(@Param("username") String username);
}
