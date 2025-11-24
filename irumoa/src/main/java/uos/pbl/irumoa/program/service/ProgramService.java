package uos.pbl.irumoa.program.service;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uos.pbl.irumoa.program.dto.NoticeClickRequest;
import uos.pbl.irumoa.program.dto.NoticeSearchPageResponse;
import uos.pbl.irumoa.program.dto.NoticeSearchRequest;
import uos.pbl.irumoa.program.dto.NoticeSearchResponse;
import uos.pbl.irumoa.program.entity.ActionLog;
import uos.pbl.irumoa.program.entity.Program;
import uos.pbl.irumoa.program.repository.ActionLogRepository;
import uos.pbl.irumoa.program.repository.ProgramRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ProgramService {

    private final ProgramRepository programRepository;
    private final ActionLogRepository actionLogRepository;

    public ProgramService(ProgramRepository programRepository, ActionLogRepository actionLogRepository) {
        this.programRepository = programRepository;
        this.actionLogRepository = actionLogRepository;
    }

    public NoticeSearchPageResponse searchNotices(NoticeSearchRequest request) {
        Specification<Program> spec = buildSpecification(request);
        
        Pageable pageable = PageRequest.of(
            request.getPage(), 
            request.getSize(),
            Sort.by(Sort.Direction.DESC, "appStartDate")
        );
        
        Page<Program> programPage = programRepository.findAll(spec, pageable);
        
        List<NoticeSearchResponse> content = programPage.getContent().stream()
                .map(NoticeSearchResponse::from)
                .collect(Collectors.toList());
        
        return NoticeSearchPageResponse.of(
                content,
                request.getPage(),
                request.getSize(),
                programPage.getTotalElements()
        );
    }

    private Specification<Program> buildSpecification(NoticeSearchRequest request) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            boolean needsDistinct = false;

            // "제한없음" 값 정의
            final int UNRESTRICTED_GRADE = 0;
            final String UNRESTRICTED_DEPARTMENT = "제한없음";

            // state 필터링 (appStartDate, appEndDate 기반 동적 계산)
            String state = request.getState();
            if (state != null && !state.trim().isEmpty()) {
                LocalDate now = LocalDate.now();

                if ("모집예정".equals(state)) {
                    // appStartDate > now
                    predicate = cb.and(predicate, 
                        cb.greaterThan(root.get("appStartDate"), now)
                    );
                } else if ("모집중".equals(state)) {
                    // appStartDate <= now AND appEndDate >= now
                    Predicate startDatePredicate = cb.lessThanOrEqualTo(root.get("appStartDate"), now);
                    Predicate endDatePredicate = cb.greaterThanOrEqualTo(root.get("appEndDate"), now);
                    predicate = cb.and(predicate, startDatePredicate, endDatePredicate);
                } else if ("모집완료".equals(state)) {
                    // appEndDate < now
                    predicate = cb.and(predicate, 
                        cb.lessThan(root.get("appEndDate"), now)
                    );
                }
            }

            // department 필터링 (사용자 학과 또는 제한없음)
            List<String> departments = request.getDepartment();
            if (departments != null && !departments.isEmpty()) {
                Join<Program, String> departmentJoin = root.join("departments", JoinType.INNER);
                
                // 1. 사용자의 학과가 프로그램 학과 목록에 포함되거나
                Predicate userDeptPredicate = departmentJoin.in(departments);
                // 2. 프로그램의 학과가 "제한없음"이거나
                Predicate unrestrictedDeptPredicate = cb.equal(departmentJoin, UNRESTRICTED_DEPARTMENT);
                
                // 1 또는 2
                predicate = cb.and(predicate, cb.or(userDeptPredicate, unrestrictedDeptPredicate));
                needsDistinct = true;
            }

            // grade 필터링 (사용자 학년 또는 제한없음)
            // filter가 명시적으로 true일 때만 학년 필터를 적용
            if (Boolean.TRUE.equals(request.getFilter()) && request.getGrade() != null) {
                Join<Program, Integer> gradeJoin = root.join("grades", JoinType.INNER);
                Integer userGrade = request.getGrade();
                
                // 1. 사용자의 학년이 프로그램 학년 목록에 포함되거나
                Predicate userGradePredicate = cb.equal(gradeJoin, userGrade);
                // 2. 프로그램의 학년이 0("제한없음")이거나
                Predicate unrestrictedGradePredicate = cb.equal(gradeJoin, UNRESTRICTED_GRADE);
                
                // 1 또는 2
                predicate = cb.and(predicate, cb.or(userGradePredicate, unrestrictedGradePredicate));
                needsDistinct = true;
            }

            // category 필터링
            List<String> categories = request.getCategory();
            if (categories != null && !categories.isEmpty()) {
                Join<Program, String> categoryJoin = root.join("categories", JoinType.INNER);
                predicate = cb.and(predicate, categoryJoin.in(categories));
                needsDistinct = true;
            }

            // interests 필터링 (category와 동일하게 처리)
            List<String> interests = request.getInterests();
            if (interests != null && !interests.isEmpty()) {
                Join<Program, String> interestJoin = root.join("categories", JoinType.INNER);
                predicate = cb.and(predicate, interestJoin.in(interests));
                needsDistinct = true;
            }

            // keyword 검색 (제목, 내용, 부서에 LIKE 검색)
            String keyword = request.getTrimmedKeyword();
            if (!keyword.isEmpty()) {
                String lowerKeyword = "%" + keyword.toLowerCase() + "%";
                Predicate titlePredicate = cb.like(cb.lower(root.get("title")), lowerKeyword);
                Predicate contentPredicate = cb.like(cb.lower(root.get("content")), lowerKeyword);
                
                // 부서 검색을 위한 조인
                Join<Program, String> deptJoin = root.join("departments", JoinType.LEFT);
                Predicate deptPredicate = cb.like(cb.lower(deptJoin), lowerKeyword);
                
                predicate = cb.and(predicate, cb.or(titlePredicate, contentPredicate, deptPredicate));
                needsDistinct = true;
            }

            // distinct 설정
            if (needsDistinct && query != null) {
                query.distinct(true);
            }

            return predicate;
        };
    }

    @Transactional
    public void clickNotice(NoticeClickRequest request) {
        ActionLog actionLog = new ActionLog();
        actionLog.setProgramId(request.getId());
        actionLog.setGrade(request.getGrade());
        
        // department를 JSON 문자열로 저장
        actionLog.setDepartmentList(request.getDepartment() != null ? request.getDepartment() : List.of());
        
        // interests를 JSON 문자열로 저장
        actionLog.setInterestsList(request.getInterests() != null ? request.getInterests() : List.of());
        
        actionLogRepository.save(actionLog);
    }
}

