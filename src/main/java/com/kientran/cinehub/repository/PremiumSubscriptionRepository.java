package com.kientran.cinehub.repository;

import com.kientran.cinehub.entity.PremiumSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PremiumSubscriptionRepository extends JpaRepository<PremiumSubscription, Long> {
    List<PremiumSubscription> findByUserId(Long userId);
}