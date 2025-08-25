package kr.ac.kumoh.likelion.gugu.care.application;

import kr.ac.kumoh.likelion.gugu.care.domain.*;
import kr.ac.kumoh.likelion.gugu.care.domain.type.ApplicationStatus;
import kr.ac.kumoh.likelion.gugu.care.domain.type.RequestStatus;
import kr.ac.kumoh.likelion.gugu.care.infra.CareApplicationRepository;
import kr.ac.kumoh.likelion.gugu.care.infra.CareMatchRepository;
import kr.ac.kumoh.likelion.gugu.care.infra.CareRequestRepository;
import kr.ac.kumoh.likelion.gugu.care.infra.CareRequestTagRepository;
import kr.ac.kumoh.likelion.gugu.care.web.dto.request.CreateCareRequestDto;
import kr.ac.kumoh.likelion.gugu.care.tag.Tag;
import kr.ac.kumoh.likelion.gugu.care.tag.TagRepository;
import kr.ac.kumoh.likelion.gugu.user.domain.User;
import kr.ac.kumoh.likelion.gugu.user.infra.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CareRequestService {

    private final CareRequestRepository careRequestRepo;
    private final UserRepository userRepo;
    private final TagRepository tagRepo;
    private final CareRequestTagRepository crtRepo;
    private final CareMatchRepository careMatchRepo;
    private final CareRequestRepository requestRepo;
    private final CareApplicationRepository appRepo;

    @Transactional
    public Long create(Long parentId, CreateCareRequestDto dto) {
        User parent = userRepo.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("parent not found"));

        CareRequest cr = new CareRequest();
        cr.setParent(parent);
        cr.setTitle(dto.getTitle());
        cr.setCategory(dto.getCategory());
        cr.setDateOnly(dto.getDateOnly());
        cr.setStartTime(dto.getStartTime());
        cr.setEndTime(dto.getEndTime());
        cr.setLocation(dto.getLocation());
        cr.setChildGender(dto.getChildGender());
        cr.setChildAge(dto.getChildAge());
        cr.setDescription(dto.getDescription());

        cr = careRequestRepo.save(cr);

        if (dto.getTags() != null) {
            for (String name : dto.getTags()) {
                Tag tag = tagRepo.findByName(name)
                        .orElseGet(() -> {
                            Tag t = new Tag();
                            t.setName(name);
                            return tagRepo.save(t);
                        });

                CareRequestTag link = new CareRequestTag();
                CareRequestTagId id = new CareRequestTagId();
                id.setRequestId(cr.getId());
                id.setTagId(tag.getId());
                link.setId(id);
                link.setRequest(cr);
                link.setTag(tag);
                crtRepo.save(link);
            }
        }
        return cr.getId();
    }

    @Transactional
    public Long matchProvider(Long actorId, Long requestId, Long providerId) {
        // 1) 잠금 걸고 로드
        CareRequest req = requestRepo.findByIdForUpdate(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청이 존재하지 않습니다."));

        // 2) 권한: 요청자만
        if (!req.getParent().getId().equals(actorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "요청자 본인만 수락할 수 있습니다.");
        }

        // 3) 상태 검사 + 멱등 처리
        if (req.getStatus() != RequestStatus.OPEN) {
            if (req.getStatus() == RequestStatus.MATCHED && req.getMatchedProvider() != null) {
                // 같은 제공자인지 확인
                if (req.getMatchedProvider().getId().equals(providerId)) {
                    // 이미 매칭된 CareMatch가 있으면 그 id 반환(멱등)
                    return careMatchRepo.findByRequestId(requestId)
                            .map(CareMatch::getId)
                            .orElse(0L); // 혹시 없으면 0 (실제로는 UNIQUE 제약이라 존재해야 정상)
                }
                throw new ResponseStatusException(HttpStatus.GONE, "이미 다른 제공자와 매칭 완료되었습니다.");
            }
            throw new ResponseStatusException(HttpStatus.GONE, "이미 종료된 요청입니다.");
        }

        // 4) 신청자 여부 확인
        CareApplication target = appRepo.findByRequestIdAndApplicantId(requestId, providerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 사용자는 신청자가 아닙니다."));

        if (target.getStatus() != ApplicationStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "현재 상태에서 수락할 수 없습니다: " + target.getStatus());
        }

        // 5) 대상 ACCEPTED, 나머지 PENDING → REJECTED (선택)
        target.setStatus(ApplicationStatus.ACCEPTED);

        List<CareApplication> pendings = appRepo.findByRequestIdAndStatus(requestId, ApplicationStatus.PENDING);
        for (CareApplication a : pendings) {
            if (!a.getApplicant().getId().equals(providerId)) {
                a.setStatus(ApplicationStatus.REJECTED);
            }
        }

        // 6) 제공자 스케줄 오버랩 방지
        boolean overlaps = careMatchRepo.existsOverlappingMatch(
                providerId, req.getDateOnly(), req.getStartTime(), req.getEndTime());
        if (overlaps) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "해당 제공자는 같은 시간대에 이미 매칭이 있습니다.");
        }

        // 7) 요청 상태 업데이트
        req.setStatus(RequestStatus.MATCHED);
        req.setMatchedProvider(target.getApplicant());
        req.setMatchedAt(LocalDateTime.now());

        // 8) CareMatch 생성 (멱등/중복 방지)
        if (careMatchRepo.existsByRequestId(req.getId())) {
            // 동시성으로 이미 만들어진 경우 기존 matchId 반환
            return careMatchRepo.findByRequestId(req.getId())
                    .map(CareMatch::getId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "매칭 상태 불일치"));
        }

        CareMatch match = new CareMatch(req, target.getApplicant());
        match = careMatchRepo.save(match);
        return match.getId(); // ← 컨트롤러의 "matchId"와 일관
    }
}
