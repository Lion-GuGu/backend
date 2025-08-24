package kr.ac.kumoh.likelion.gugu.care.application;

import kr.ac.kumoh.likelion.gugu.care.domain.CareMatch;
import kr.ac.kumoh.likelion.gugu.care.domain.CareRequest;
import kr.ac.kumoh.likelion.gugu.care.domain.CareRequestTag;
import kr.ac.kumoh.likelion.gugu.care.domain.CareRequestTagId;
import kr.ac.kumoh.likelion.gugu.care.domain.type.RequestStatus;
import kr.ac.kumoh.likelion.gugu.care.infra.CareMatchRepository;
import kr.ac.kumoh.likelion.gugu.care.infra.CareRequestRepository;
import kr.ac.kumoh.likelion.gugu.care.infra.CareRequestTagRepository;
import kr.ac.kumoh.likelion.gugu.care.web.dto.request.CreateCareRequestDto;
import kr.ac.kumoh.likelion.gugu.care.tag.Tag;
import kr.ac.kumoh.likelion.gugu.care.tag.TagRepository;
import kr.ac.kumoh.likelion.gugu.user.domain.User;
import kr.ac.kumoh.likelion.gugu.user.infra.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CareRequestService {

    private final CareRequestRepository careRequestRepo;
    private final UserRepository userRepo;
    private final TagRepository tagRepo;
    private final CareRequestTagRepository crtRepo;
    private final CareMatchRepository careMatchRepo;

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
        // 1. 필요한 엔티티들을 조회
        CareRequest request = careRequestRepo.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 요청입니다."));

        User provider = userRepo.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자(보호자)입니다."));

        // 2. 비즈니스 규칙 검증
        // 요청자 본인만 매칭을 확정할 수 있음
        if (!request.getParent().getId().equals(actorId)) {
            throw new SecurityException("요청자 본인만 매칭을 확정할 수 있습니다.");
        }

        // 'OPEN' 상태인 요청만 매칭 가능
        if (request.getStatus() != RequestStatus.OPEN) {
            throw new IllegalStateException("이미 매칭되었거나 마감된 요청입니다.");
        }

        // 자기 자신을 보호자로 매칭할 수 없음
        if (actorId.equals(providerId)) {
            throw new IllegalArgumentException("자기 자신을 보호자로 지정할 수 없습니다.");
        }

        // 3. 매칭 정보 생성 및 저장
        CareMatch match = new CareMatch(request, provider);
        careMatchRepo.save(match);

        // 4. 돌봄 요청의 상태를 'MATCHED'로 변경
        request.setStatus(RequestStatus.MATCHED);


        return match.getId();
    }
}
