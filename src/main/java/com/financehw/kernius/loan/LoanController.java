package com.financehw.kernius.loan;

import com.financehw.kernius.auth.entity.AuthenticatedProfile;
import com.financehw.kernius.loan.dto.request.LoanApplicationRequest;
import com.financehw.kernius.loan.dto.response.LoanResponse;
import com.financehw.kernius.loan.mapper.LoanMapper;
import com.financehw.kernius.utils.HttpReqRespUtils;
import com.financehw.kernius.utils.ListUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/loan")
@Tag(name = "Loan", description = "Loan API")
public class LoanController {
  private final LoanService loanService;

  public LoanController(LoanService loanService) {
    this.loanService = loanService;
  }

  @Transactional
  @GetMapping("/client/all")
  @Operation(summary = "Get history of all loan applications for a client", method = "GET")
  public List<LoanResponse> findAllClientLoans(AuthenticatedProfile profile) {

    return ListUtil.map(
        loanService.findAllClientLoansByClientId(profile.getId()), LoanMapper::toResponse);
  }

  @Transactional
  @GetMapping("/client")
  @Operation(summary = "Get history of approved loans for a client", method = "GET")
  public List<LoanResponse> findAllClientApprovedLoans(AuthenticatedProfile profile) {
    return ListUtil.map(
        loanService.findAllApprovedLoansByClientId(profile.getId()), LoanMapper::toResponse);
  }

  @PostMapping
  @Operation(summary = "Apply for a loan", method = "POST")
  public LoanResponse applyForLoan(
      @RequestBody LoanApplicationRequest request, AuthenticatedProfile profile) {
    if (request.getIpAddress() == null) {
      String ipAddress = HttpReqRespUtils.getClientIpAddressIfServletRequestExist();
      request.setIpAddress(ipAddress);
    }
    return LoanMapper.toResponse(loanService.createLoan(request, profile.getId()));
  }
}
