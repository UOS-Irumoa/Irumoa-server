package uos.pbl.irumoa.program.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "program")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Program {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 500)
    private String link;

    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private LocalDate appStartDate;
    private LocalDate appEndDate;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "program_category",
            joinColumns = @JoinColumn(name = "program_id"),
            uniqueConstraints = @UniqueConstraint(
                    name = "uk_program_category",
                    columnNames = {"program_id", "category"}
            )
    )
    @Column(name = "category", length = 100, nullable = false)
    private Set<String> categories = new LinkedHashSet<>();


    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "program_department",
            joinColumns = @JoinColumn(name = "program_id"),
            uniqueConstraints = @UniqueConstraint(
                    name = "uk_program_department",
                    columnNames = {"program_id", "department"}
            )
    )
    @Column(name = "department", length = 200, nullable = false)
    private Set<String> departments = new LinkedHashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "program_grade",
            joinColumns = @JoinColumn(name = "program_id"),
            uniqueConstraints = @UniqueConstraint(
                    name = "uk_program_grade",
                    columnNames = {"program_id", "grade"}
            )
    )
    @Column(name = "grade", nullable = false)
    private Set<Integer> grades = new LinkedHashSet<>();
}
