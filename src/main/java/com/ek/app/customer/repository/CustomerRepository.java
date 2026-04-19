package com.ek.app.customer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ek.app.customer.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByCpId(String cpId);

    @Query("""
            select c
            from Customer c
            where lower(c.name) like lower(concat('%', :query, '%'))
                    or lower(coalesce(c.cpId, '')) like lower(concat('%', :query, '%'))
               or lower(c.phone) like lower(concat('%', :query, '%'))
               or lower(coalesce(c.gstin, '')) like lower(concat('%', :query, '%'))
            order by c.name asc
            """)
    List<Customer> search(@Param("query") String query);
}
