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
public class NoticeClickRequest {
    private List<String> department;  // 학과 목록
    private Integer grade;
    private List<String> interests;  // 관심 분야 목록
    private Long id;  // 공지사항 id
}

