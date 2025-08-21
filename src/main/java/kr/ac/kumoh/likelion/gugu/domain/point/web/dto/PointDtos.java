package kr.ac.kumoh.likelion.gugu.domain.point.web.dto;

import kr.ac.kumoh.likelion.gugu.domain.point.PointTransaction;

import java.time.Instant;
import java.util.List;

/** 포인트 API에서 쓰는 요청/응답 DTO 모음 */
public class PointDtos {

    /** 잔액 응답 */
    public record BalanceRes(long balance) {}

    /** 거래 이력 1건 */
    public record TxRes(
            Long id, long amount, String type, String reasonCode,
            String refType, Long refId, Instant createdAt
    ) {
        public static TxRes from(PointTransaction tx) {
            String type = tx.getType() != null ? tx.getType().name() : null;
            String reason = tx.getReasonCode() != null ? tx.getReasonCode().name() : null;
            return new TxRes(
                    tx.getId(),
                    tx.getAmount(),
                    type,
                    reason,
                    tx.getRefType(),
                    tx.getRefId(),
                    tx.getCreatedAt()
            );
        }
    }

    /** 간단한 페이징 응답 래퍼 */
    public record PageRes<T>(List<T> content, int page, int size, long totalElements) {}

    /** 금액 지정 적립/차감 요청 */
    public record EarnReq(long amount, String reasonCode, String refType, Long refId) {}
    public record SpendReq(long amount, String reasonCode, String refType, Long refId) {}

    /** 규칙 기반 적립 요청 */
    public record EarnByRuleReq(String rule, String refType, Long refId) {}
}