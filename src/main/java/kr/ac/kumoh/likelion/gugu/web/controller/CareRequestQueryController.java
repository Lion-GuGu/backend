package kr.ac.kumoh.likelion.gugu.web.controller;

import kr.ac.kumoh.likelion.gugu.domain.request.CareRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class CareRequestQueryController {
    private final CareRequestRepository repo;

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
        return m;
    }
}