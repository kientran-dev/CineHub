package com.kientran.cinehub.repository;

import com.kientran.cinehub.entity.PremiumPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PremiumPackageRepository extends JpaRepository<PremiumPackage, Long> {
}