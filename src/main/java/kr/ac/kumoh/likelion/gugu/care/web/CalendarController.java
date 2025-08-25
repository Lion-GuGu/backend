package kr.ac.kumoh.likelion.gugu.care.web;

import kr.ac.kumoh.likelion.gugu.care.application.CalendarQueryService;
import kr.ac.kumoh.likelion.gugu.care.domain.CareMatch;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarQueryService service;

    public record CalendarItem(
            Long matchId,
            String role,            // PROVIDER or PARENT
            String title,
            String location,
            LocalDate dateOnly,
            LocalTime startTime,
            LocalTime endTime
    ) {}

    @GetMapping("/me/provider")
    public ResponseEntity<List<CalendarItem>> myProviderCalendar(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        Long userId = extractUserId(jwt);
        List<CareMatch> list = service.listForProvider(userId, from, to);
        return ResponseEntity.ok(list.stream().map(m -> toItem(m, "PROVIDER")).toList());
    }

    @GetMapping("/me/parent")
    public ResponseEntity<List<CalendarItem>> myParentCalendar(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        Long userId = extractUserId(jwt);
        List<CareMatch> list = service.listForParent(userId, from, to);
        return ResponseEntity.ok(list.stream().map(m -> toItem(m, "PARENT")).toList());
    }

    // 합쳐서 한 번에 보고 싶으면 이 엔드포인트도 추가 가능
    @GetMapping("/me")
    public ResponseEntity<List<CalendarItem>> myCalendar(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        Long userId = extractUserId(jwt);
        var a = service.listForProvider(userId, from, to).stream().map(m -> toItem(m, "PROVIDER"));
        var b = service.listForParent(userId, from, to).stream().map(m -> toItem(m, "PARENT"));
        return ResponseEntity.ok( java.util.stream.Stream.concat(a,b).sorted(
                java.util.Comparator.<CalendarItem, LocalDate>comparing(CalendarItem::dateOnly)
                        .thenComparing(CalendarItem::startTime)
        ).toList() );
    }

    private CalendarItem toItem(CareMatch m, String role) {
        var r = m.getRequest();
        return new CalendarItem(
                m.getId(),
                role,
                r.getTitle(),
                r.getLocation(),
                r.getDateOnly(),
                r.getStartTime(),
                r.getEndTime()
        );
    }

    private Long extractUserId(Jwt jwt) {
        Object claim = jwt.getClaims().get("userId");
        if (claim instanceof Number n) return n.longValue();
        if (claim instanceof String s) return Long.parseLong(s);
        throw new IllegalArgumentException("JWT에 userId 클레임이 없습니다.");
    }
}