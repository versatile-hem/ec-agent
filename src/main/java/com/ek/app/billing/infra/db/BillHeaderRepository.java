package com.ek.app.billing.infra.db;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillHeaderRepository
        extends JpaRepository<BillHeader, Long> {

    Optional<BillHeader> findByBillNo(String billNo);
}