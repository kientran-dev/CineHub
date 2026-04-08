package com.kientran.cinehub.repository;

import com.kientran.cinehub.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserId(Long userId);

    @Query(value = "SELECT EXTRACT(MONTH FROM p.payment_date) as month, EXTRACT(YEAR FROM p.payment_date) as year, SUM(p.amount) as total " +
            "FROM payments p WHERE p.status = 'SUCCESS' " +
            "GROUP BY EXTRACT(YEAR FROM p.payment_date), EXTRACT(MONTH FROM p.payment_date) " +
            "ORDER BY EXTRACT(YEAR FROM p.payment_date), EXTRACT(MONTH FROM p.payment_date)", nativeQuery = true)
    List<Object[]> findMonthlyRevenue();
}