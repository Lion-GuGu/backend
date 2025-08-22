package kr.ac.kumoh.likelion.gugu.care.web;

import kr.ac.kumoh.likelion.gugu.care.application.CareRequestService;
import kr.ac.kumoh.likelion.gugu.care.web.dto.request.CreateCareRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class CareRequestController {

    private final CareRequestService service;

    @PostMapping
    public ResponseEntity<Map<String, Long>> create(
            @RequestParam Long parentId,
            @Valid @RequestBody CreateCareRequestDto dto) {
        Long id = service.create(parentId, dto);
        return ResponseEntity.ok(Map.of("id", id));
    }
}