package com.financehw.kernius.loan;

import com.financehw.kernius.loan.entity.LoanEntity;
import com.financehw.kernius.loan.entity.LoanStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<LoanEntity, Long> {
  List<LoanEntity> findAllByClientId(Long id);

  Integer countByIpAddressAndCreatedAtBetween(
      String ipAddress, LocalDateTime start, LocalDateTime end);

  List<LoanEntity> findAllByClientIdAndStatus(Long clientId, LoanStatus status);
}
