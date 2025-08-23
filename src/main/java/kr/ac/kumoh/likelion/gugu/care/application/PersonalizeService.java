package kr.ac.kumoh.likelion.gugu.care.application;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.personalizeruntime.PersonalizeRuntimeClient;
import software.amazon.awssdk.services.personalizeruntime.model.GetPersonalizedRankingRequest;
import software.amazon.awssdk.services.personalizeruntime.model.PredictedItem;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonalizeService {

    private final PersonalizeRuntimeClient personalizeRuntimeClient;

    @Value("${personalize.campaign-arn}")
    private String campaignArn;

    public List<String> getPersonalizedRanking(String userId, List<String> candidateIds, String contextItemId) {
        if (candidateIds == null || candidateIds.isEmpty()) {
            return List.of();
        }

        GetPersonalizedRankingRequest.Builder requestBuilder = GetPersonalizedRankingRequest.builder()
                .campaignArn(campaignArn)
                .userId(userId)
                .inputList(candidateIds);

        if (contextItemId != null) {
            requestBuilder.context(Map.of("itemId", contextItemId));
        }

        try {
            return personalizeRuntimeClient.getPersonalizedRanking(requestBuilder.build())
                    .personalizedRanking().stream()
                    .map(PredictedItem::itemId)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Personalize API 호출 실패: " + e.getMessage());
            // 실패 시, 원래 후보 목록을 그대로 반환 (Fallback)
            return candidateIds;
        }
    }
}