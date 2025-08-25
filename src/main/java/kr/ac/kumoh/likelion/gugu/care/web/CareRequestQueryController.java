package kr.ac.kumoh.likelion.gugu.care.web;

import kr.ac.kumoh.likelion.gugu.care.infra.CareRequestRepository;
import kr.ac.kumoh.likelion.gugu.care.application.PersonalizeService; // 추가
import kr.ac.kumoh.likelion.gugu.user.infra.UserRepository; // 추가
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class CareRequestQueryController {
    private final CareRequestRepository repo;
    private final PersonalizeService personalizeService;
    private final UserRepository userRepository;

    @GetMapping
    public List<Map<String, Object>> all() {
        return repo.findAll().stream().map(cr -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", cr.getId());
            m.put("parent_id", cr.getParent().getId());
            m.put("title", cr.getTitle());
            m.put("category", cr.getCategory().name());
            m.put("date_only", String.valueOf(cr.getDateOnly()));
            m.put("start_time", String.valueOf(cr.getStartTime()));
            m.put("end_time", String.valueOf(cr.getEndTime()));
            m.put("status", cr.getStatus().name());
            m.put("location", cr.getLocation());
            m.put("child_gender", cr.getChildGender().name());
            m.put("child_age", cr.getChildAge());
            m.put("description", cr.getDescription());
            return m;
        }).toList(); // JDK 16 미만이면 .collect(Collectors.toList())
    }

    @GetMapping("/{id}")
    public Map<String, Object> one(@PathVariable Long id) {
        var cr = repo.findById(id).orElseThrow();
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", cr.getId());
        m.put("parent_id", cr.getParent().getId());
        m.put("title", cr.getTitle());
        m.put("category", cr.getCategory().name());
        m.put("date_only", String.valueOf(cr.getDateOnly()));
        m.put("start_time", String.valueOf(cr.getStartTime()));
        m.put("end_time", String.valueOf(cr.getEndTime()));
        m.put("status", cr.getStatus().name());
        m.put("location", cr.getLocation());
        m.put("child_gender", cr.getChildGender().name());
        m.put("child_age", cr.getChildAge());
        m.put("description", cr.getDescription());
        return m;
    }

    @GetMapping("/{requestId}/recommendations")
    public ResponseEntity<List<String>> getRecommendations(@PathVariable Long requestId) {
        // 1. 추천의 기준이 될 사용자(요청자) ID를 가져옴
        String requesterId = repo.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"))
                .getParent().getId().toString();

        // 2. 순위를 매길 후보 보호자 목록을 DB에서 조회 (예: 모든 사용자)
        // 실제 서비스에서는 '활동 가능 지역', '시간' 등으로 필터링된 후보 목록을 사용해야 함
        List<String> candidateProviderIds = userRepository.findAll().stream()
                .map(user -> user.getId().toString())
                .collect(Collectors.toList());

        // 3. Personalize 서비스에 추천 순위 요청
        List<String> recommendedIds = personalizeService.getPersonalizedRanking(
                requesterId,
                candidateProviderIds,
                requestId.toString() // 현재 요청 ID를 추천의 '맥락'으로 전달
        );

        return ResponseEntity.ok(recommendedIds);
    }
}