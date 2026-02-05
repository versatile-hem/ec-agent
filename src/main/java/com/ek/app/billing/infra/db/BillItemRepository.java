package com.ek.app.billing.infra.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillItemRepository
        extends JpaRepository<BillItem, Long> {
}