package kr.ac.kumoh.likelion.gugu.service;

import kr.ac.kumoh.likelion.gugu.domain.request.*;
import kr.ac.kumoh.likelion.gugu.domain.tag.Tag;
import kr.ac.kumoh.likelion.gugu.domain.tag.TagRepository;
import kr.ac.kumoh.likelion.gugu.domain.user.User;
import kr.ac.kumoh.likelion.gugu.domain.user.UserRepository;
import kr.ac.kumoh.likelion.gugu.web.dto.request.CreateCareRequestDto;
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
}
