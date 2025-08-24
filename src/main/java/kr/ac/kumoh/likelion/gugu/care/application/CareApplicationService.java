package kr.ac.kumoh.likelion.gugu.care.application;

import kr.ac.kumoh.likelion.gugu.care.domain.CareApplication;
import kr.ac.kumoh.likelion.gugu.care.domain.CareRequest;
import kr.ac.kumoh.likelion.gugu.care.domain.type.ApplicationStatus;
import kr.ac.kumoh.likelion.gugu.care.domain.type.RequestStatus;
import kr.ac.kumoh.likelion.gugu.care.infra.CareApplicationRepository;
import kr.ac.kumoh.likelion.gugu.care.infra.CareRequestRepository;
import kr.ac.kumoh.likelion.gugu.user.domain.User;
import kr.ac.kumoh.likelion.gugu.user.infra.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CareApplicationService {

    private final CareRequestRepository requestRepo;
    private final CareApplicationRepository appRepo;
    private final UserRepository userRepo;

    /**
     * 신청하기 (다른 사용자가 OPEN 상태의 요청에 신청)
     */
    // CareApplicationService.java

    @Transactional
    public Long apply(Long applicantId, Long requestId) {
        CareRequest req = requestRepo.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청이 존재하지 않습니다."));
        User applicant = userRepo.findById(applicantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자가 존재하지 않습니다."));

        log.info("[apply] requestId={}, status={}", req.getId(), req.getStatus());

        if (req.getStatus() != RequestStatus.OPEN) {
            throw new ResponseStatusException(HttpStatus.GONE, "모집이 종료되었거나 매칭된 요청입니다.");
        }
        if (req.getParent().getId().equals(applicantId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "본인 요청에는 신청할 수 없습니다.");
        }
        if (appRepo.existsByRequestIdAndApplicantId(requestId, applicantId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 신청한 요청입니다.");
        }

        try {
            CareApplication app = new CareApplication(req, applicant);
            appRepo.save(app);
            return app.getId();
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 신청한 요청입니다.", e);
        }
    }


    /**
     * 신청 취소 (본인만)
     */
    @Transactional
    public void withdraw(Long applicantId, Long requestId) {
        CareApplication app = appRepo.findByRequestIdAndApplicantId(requestId, applicantId)
                .orElseThrow(() -> new IllegalArgumentException("신청 기록이 없습니다."));

        if (!app.getApplicant().getId().equals(applicantId)) {
            throw new SecurityException("본인의 신청만 취소할 수 있습니다.");
        }
        if (app.getStatus() != ApplicationStatus.PENDING) {
            throw new IllegalStateException("이미 취소되었거나 처리된 신청입니다.");
        }
        app.setStatus(ApplicationStatus.WITHDRAWN);
    }

    /**
     * 신청자 목록 조회 (요청자 본인만)
     */
    @Transactional(readOnly = true)
    public List<CareApplication> listApplicants(Long actorId, Long requestId) {
        CareRequest req = requestRepo.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("요청이 존재하지 않습니다."));
        if (!req.getParent().getId().equals(actorId)) {
            throw new SecurityException("요청자 본인만 신청자 목록을 볼 수 있습니다.");
        }
        return appRepo.findByRequestIdOrderByIdDesc(requestId);
    }

    /** (선택) provider가 실제 신청자인지 확인 */
    @Transactional(readOnly = true)
    public boolean isApplicantOf(Long requestId, Long providerId) {
        return appRepo.existsByRequestIdAndApplicantId(requestId, providerId);
    }
}
