package com.fasttime.domain.bootcamp.controller;

import com.fasttime.domain.bootcamp.dto.request.CertificationRequestDTO;
import com.fasttime.domain.bootcamp.dto.request.WithdrawalRequestDTO;
import com.fasttime.domain.bootcamp.dto.response.CertificationResponseDTO;
import com.fasttime.domain.bootcamp.dto.response.MyCertificationResponseDTO;
import com.fasttime.domain.bootcamp.entity.Certification;
import com.fasttime.domain.bootcamp.service.CertificationService;
import com.fasttime.global.util.ResponseDTO;
import com.fasttime.global.util.SecurityUtil;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/certification")
@RequiredArgsConstructor
public class CertificationController {

    private final CertificationService certificationService;
    private final SecurityUtil securityUtil;

    @PostMapping
    public ResponseEntity<ResponseDTO<CertificationResponseDTO>> createCertification(
        @RequestBody CertificationRequestDTO requestDTO) {
        Long currentMemberId = securityUtil.getCurrentMemberId();
        Certification certification = certificationService.createCertification(requestDTO,
            currentMemberId);

        CertificationResponseDTO responseDto = CertificationResponseDTO.from(certification);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ResponseDTO.res(HttpStatus.OK, "인증 요청이 완료되었습니다.", responseDto));
    }

    @PostMapping("/withdraw/{certificationId}")
    public ResponseEntity<ResponseDTO<CertificationResponseDTO>> withdrawCertification(
        @PathVariable Long certificationId,
        @Valid @RequestBody WithdrawalRequestDTO withdrawalRequestDTO) {
        Long currentMemberId = securityUtil.getCurrentMemberId();
        Certification certification = certificationService.withdrawCertification(
            certificationId, currentMemberId, withdrawalRequestDTO.withdrawalReason());

        CertificationResponseDTO responseDto = CertificationResponseDTO.from(certification);

        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDTO.res(HttpStatus.OK, "철회 요청이 완료되었습니다.", responseDto));
    }
}
