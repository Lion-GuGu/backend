package kr.ac.kumoh.likelion.gugu.care.application;

import kr.ac.kumoh.likelion.gugu.care.domain.CareMatch;
import kr.ac.kumoh.likelion.gugu.care.infra.CareMatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarQueryService {

    private final CareMatchRepository matchRepo;

    @Transactional(readOnly = true)
    public List<CareMatch> listForProvider(Long userId, LocalDate from, LocalDate to) {
        return matchRepo.findForProviderBetween(userId, from, to);
    }

    @Transactional(readOnly = true)
    public List<CareMatch> listForParent(Long userId, LocalDate from, LocalDate to) {
        return matchRepo.findForParentBetween(userId, from, to);
    }
}