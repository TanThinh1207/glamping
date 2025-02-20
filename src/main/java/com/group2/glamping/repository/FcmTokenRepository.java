package com.group2.glamping.repository;

import com.group2.glamping.model.entity.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Integer> {
    List<FcmToken> findByUserId(int userId);

    boolean existsByTokenAndUserId(String token, int userId);

    void deleteByTokenAndUserId(String token, int userId);
}
