package kr.ac.kumoh.likelion.gugu;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/**
 * 공통 예외 → 명확한 HTTP 상태코드로 매핑
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Map<String, Object>> body(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of(
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", message
        ));
    }

    /** 서비스에서 던진 ResponseStatusException은 상태/메시지 그대로 유지 */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> on(ResponseStatusException ex) {
        return body(HttpStatus.valueOf(ex.getStatusCode().value()),
                ex.getReason() != null ? ex.getReason() : ex.getMessage());
    }

    /** 잘못된 입력 */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> on(IllegalArgumentException ex) {
        return body(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /** 상태 충돌, 중복 신청 등 */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> on(IllegalStateException ex) {
        return body(HttpStatus.CONFLICT, ex.getMessage()); // 409
    }

    /** 권한 문제 */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, Object>> on(SecurityException ex) {
        return body(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    /** DB 유니크 제약 위반 등 → 중복 신청 2차 방어 */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> on(DataIntegrityViolationException ex) {
        return body(HttpStatus.CONFLICT, "데이터 무결성 위반(중복 등)");
    }

    /** 마지막 안전망: 500 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> on(Exception ex) {
        return body(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }
}
