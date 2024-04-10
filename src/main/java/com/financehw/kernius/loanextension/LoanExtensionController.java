package com.financehw.kernius.loanextension;

import com.financehw.kernius.auth.entity.AuthenticatedProfile;
import com.financehw.kernius.loanextension.dto.request.LoanExtensionRequest;
import com.financehw.kernius.loanextension.dto.response.LoanExtensionResponse;
import com.financehw.kernius.loanextension.mapper.LoanExtensionMapper;
import com.financehw.kernius.utils.ListUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@Transactional
@RestController
@RequestMapping("/api/v1/loan-extension")
@Tag(name = "LoanExtension", description = "Loan extension API")
public class LoanExtensionController {
  private final LoanExtensionService loanExtensionService;

  public LoanExtensionController(LoanExtensionService loanExtensionService) {
    this.loanExtensionService = loanExtensionService;
  }

  @PostMapping
  @Operation(summary = "Submit a loan extension", method = "POST")
  public LoanExtensionResponse extendLoan(
      @RequestBody LoanExtensionRequest request, AuthenticatedProfile profile) {
    return LoanExtensionMapper.toResponse(
        loanExtensionService.extendLoan(request, profile.getId()));
  }

  @GetMapping("/client")
  @Operation(summary = "Get loan extension history for a client", method = "GET")
  public List<LoanExtensionResponse> findAllClientLoanExtensions(AuthenticatedProfile profile) {
    return ListUtil.map(
        loanExtensionService.findAllByClientId(profile.getId()), LoanExtensionMapper::toResponse);
  }
}
