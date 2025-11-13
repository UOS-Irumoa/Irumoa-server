package uos.pbl.irumoa.program.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import uos.pbl.irumoa.program.entity.Program;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeSearchResponse {
    private Long id;
    private String title;
    private String link;
    private String content;
    private LocalDate appStartDate;
    private LocalDate appEndDate;
    private Set<String> categories;
    private Set<String> departments;
    private Set<Integer> grades;

    public static NoticeSearchResponse from(Program program) {
        NoticeSearchResponse response = new NoticeSearchResponse();
        response.setId(program.getId());
        response.setTitle(program.getTitle());
        response.setLink(program.getLink());
        response.setContent(program.getContent());
        response.setAppStartDate(program.getAppStartDate());
        response.setAppEndDate(program.getAppEndDate());
        response.setCategories(program.getCategories());
        response.setDepartments(program.getDepartments());
        response.setGrades(program.getGrades());
        return response;
    }
}

