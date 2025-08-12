package kr.ac.kumoh.likelion.gugu.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.ac.kumoh.likelion.gugu.domain.request.Category;
import kr.ac.kumoh.likelion.gugu.domain.request.ChildGender;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class CreateCareRequestDto {
    @NotBlank
    private String title;
    @NotNull
    private Category category;
    @NotNull private LocalDate dateOnly;
    @NotNull private LocalTime startTime;
    @NotNull private LocalTime endTime;
    private String location;
    @NotNull private ChildGender childGender;
    @NotNull private Integer childAge;
    private String description;
    private List<String> tags; // ["야외","축구"]
}