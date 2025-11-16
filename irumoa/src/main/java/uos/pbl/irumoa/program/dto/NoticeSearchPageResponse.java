package uos.pbl.irumoa.program.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeSearchPageResponse {
    private List<NoticeSearchResponse> content; // 현재 페이지 공지사항 목록
    private int page;
    private int size;
    private long totalElements; // 검색된 총 개수
    private int totalPages;

    public static NoticeSearchPageResponse of(List<NoticeSearchResponse> content, int page, int size, long totalElements) {
        NoticeSearchPageResponse response = new NoticeSearchPageResponse();
        response.setContent(content);
        response.setPage(page);
        response.setSize(size);
        response.setTotalElements(totalElements);
        response.setTotalPages((int) Math.ceil((double) totalElements / size));
        return response;
    }
}

