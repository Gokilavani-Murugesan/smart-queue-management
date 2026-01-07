package com.smartqueue.repository;

import com.smartqueue.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    // Find last token number for combination
    Optional<Token> findTopByCategoryAndServiceAndDepartmentAndSubserviceOrderByTokenNumberDesc(
            String category, String service, String department, String subservice);

    // Find all tokens by category
    List<Token> findByCategory(String category);

    // Waiting tokens for combination
    List<Token> findByCategoryAndServiceAndDepartmentAndSubserviceAndStatusOrderByTokenNumberAsc(
            String category, String service, String department, String subservice, String status);
}
