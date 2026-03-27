package cn.academic.cloud.paper.controller;

import cn.academic.cloud.common.core.Result;
import cn.academic.cloud.common.dto.PaperSimpleDTO;
import cn.academic.cloud.paper.service.PaperService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/paper/internal")
public class InternalPaperController {

    private final PaperService paperService;

    public InternalPaperController(PaperService paperService) {
        this.paperService = paperService;
    }

    @PostMapping("/by-ids")
    public Result<List<PaperSimpleDTO>> byIds(@RequestBody List<Long> ids) {
        return Result.success(paperService.listSimpleByIds(ids));
    }

    @GetMapping("/latest")
    public Result<List<PaperSimpleDTO>> latest(@RequestParam(value = "limit", defaultValue = "10") int limit) {
        return Result.success(paperService.latestSimple(limit));
    }

    @GetMapping("/search")
    public Result<List<PaperSimpleDTO>> search(@RequestParam(value = "keyword", required = false) String keyword,
                                               @RequestParam(value = "limit", defaultValue = "10") int limit) {
        return Result.success(paperService.searchSimple(keyword, limit));
    }
}
