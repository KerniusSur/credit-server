package com.financehw.kernius.loanextension;

import com.financehw.kernius.loanextension.entity.LoanExtensionEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanExtensionRepository extends JpaRepository<LoanExtensionEntity, Long> {
  List<LoanExtensionEntity> findAllByLoanClientId(Long clientId);
}
