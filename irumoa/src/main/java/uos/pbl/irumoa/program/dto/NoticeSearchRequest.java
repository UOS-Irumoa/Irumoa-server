package uos.pbl.irumoa.program.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeSearchRequest {
    private List<String> department;  // 학과 목록
    private Integer grade;
    private List<String> interests;  // 관심 분야 목록
    private Boolean filter;  // null 허용
    private String keyword = "";  // 제목/요약/부서에 LIKE 검색
    private List<String> category;  // 카테고리 목록
    private String state;
    
    @Min(value = 0, message = "페이지는 0 이상이어야 합니다")
    private Integer page = 0;
    
    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
    private Integer size = 15;

    public String getTrimmedKeyword() {
        if (keyword == null) {
            return "";
        }
        return keyword.trim();
    }
}

