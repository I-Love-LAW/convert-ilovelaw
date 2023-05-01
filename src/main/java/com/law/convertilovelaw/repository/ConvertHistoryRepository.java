package com.law.convertilovelaw.repository;

import com.law.convertilovelaw.model.ConvertHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface ConvertHistoryRepository extends JpaRepository<ConvertHistory, String> {
    Optional<ConvertHistory> findByUsername(String username);
    ArrayList<ConvertHistory> findAllByUsername(String username);

    Boolean existsByUsername(String username);
}
