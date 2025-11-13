package uos.pbl.irumoa.program.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "action_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "program_id", nullable = false)
    private Long programId;

    @Lob
    @Column(name = "department", columnDefinition = "TEXT", nullable = false)
    private String department;  // JSON 문자열로 저장

    @Column(name = "grade", nullable = false)
    private Integer grade;

    @Lob
    @Column(name = "interests", columnDefinition = "TEXT", nullable = false)
    private String interests;  // JSON 문자열로 저장

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Helper methods for List conversion
    public List<String> getDepartmentList() {
        if (department == null || department.trim().isEmpty() || department.equals("[]")) {
            return new ArrayList<>();
        }
        // JSON 배열 파싱: ["value1", "value2"] 형태
        String cleaned = department.trim();
        if (cleaned.startsWith("[") && cleaned.endsWith("]")) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }
        List<String> result = new ArrayList<>();
        if (!cleaned.isEmpty()) {
            String[] parts = cleaned.split(",");
            for (String part : parts) {
                String trimmed = part.trim().replaceAll("^\"|\"$", "");
                if (!trimmed.isEmpty()) {
                    result.add(trimmed);
                }
            }
        }
        return result;
    }

    public void setDepartmentList(List<String> departments) {
        if (departments == null || departments.isEmpty()) {
            this.department = "[]";
        } else {
            this.department = "[\"" + String.join("\",\"", departments) + "\"]";
        }
    }

    public List<String> getInterestsList() {
        if (interests == null || interests.trim().isEmpty() || interests.equals("[]")) {
            return new ArrayList<>();
        }
        // JSON 배열 파싱: ["value1", "value2"] 형태
        String cleaned = interests.trim();
        if (cleaned.startsWith("[") && cleaned.endsWith("]")) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }
        List<String> result = new ArrayList<>();
        if (!cleaned.isEmpty()) {
            String[] parts = cleaned.split(",");
            for (String part : parts) {
                String trimmed = part.trim().replaceAll("^\"|\"$", "");
                if (!trimmed.isEmpty()) {
                    result.add(trimmed);
                }
            }
        }
        return result;
    }

    public void setInterestsList(List<String> interests) {
        if (interests == null || interests.isEmpty()) {
            this.interests = "[]";
        } else {
            this.interests = "[\"" + String.join("\",\"", interests) + "\"]";
        }
    }
}

