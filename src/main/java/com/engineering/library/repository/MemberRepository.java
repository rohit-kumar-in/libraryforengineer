package com.engineering.library.repository;

import com.engineering.library.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByRollNumber(String rollNumber);
    Optional<Member> findByEmail(String email);
    boolean existsByRollNumber(String rollNumber);
    boolean existsByEmail(String email);
}
