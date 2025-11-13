package uos.pbl.irumoa.program.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uos.pbl.irumoa.program.dto.NoticeClickRequest;
import uos.pbl.irumoa.program.dto.NoticeSearchPageResponse;
import uos.pbl.irumoa.program.dto.NoticeSearchRequest;
import uos.pbl.irumoa.program.service.ProgramService;

@RestController
@RequestMapping("/notices")
public class ProgramController {

    private final ProgramService programService;

    public ProgramController(ProgramService programService) {
        this.programService = programService;
    }

    @GetMapping("/search")
    public ResponseEntity<NoticeSearchPageResponse> searchNotices(
            @ModelAttribute @Valid NoticeSearchRequest request
    ) {
        NoticeSearchPageResponse response = programService.searchNotices(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/click")
    public ResponseEntity<Void> clickNotice(
            @RequestBody NoticeClickRequest request
    ) {
        programService.clickNotice(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

