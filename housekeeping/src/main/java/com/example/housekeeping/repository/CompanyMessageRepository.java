package com.example.housekeeping.repository;

import com.example.housekeeping.entity.CompanyMessage;
import com.example.housekeeping.entity.ServiceOrder;
import com.example.housekeeping.entity.UserAll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyMessageRepository extends JpaRepository<CompanyMessage, Long> {

    List<CompanyMessage> findByOrderOrderByCreatedAtAsc(ServiceOrder order);

    List<CompanyMessage> findByOrderAndCreatedAtAfterOrderByCreatedAtAsc(ServiceOrder order, Instant createdAt);

    Optional<CompanyMessage> findFirstByOrderOrderByCreatedAtDesc(ServiceOrder order);

    long countByOrderAndRecipientAndReadAtIsNull(ServiceOrder order, UserAll recipient);

    List<CompanyMessage> findByOrderAndRecipientAndReadAtIsNull(ServiceOrder order, UserAll recipient);

    void deleteByOrder(ServiceOrder order);

    void deleteByOrderIn(List<ServiceOrder> orders);

    void deleteBySenderOrRecipient(UserAll sender, UserAll recipient);
}
