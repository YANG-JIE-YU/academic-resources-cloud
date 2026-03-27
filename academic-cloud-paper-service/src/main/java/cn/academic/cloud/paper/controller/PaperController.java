package cn.academic.cloud.paper.controller;

import cn.academic.cloud.common.core.Result;
import cn.academic.cloud.paper.domain.dto.PaperSaveRequest;
import cn.academic.cloud.paper.domain.entity.PaperEntity;
import cn.academic.cloud.paper.service.PaperService;
import cn.academic.cloud.paper.support.UserHeaderContext;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/paper/papers")
public class PaperController {

    private final PaperService paperService;

    public PaperController(PaperService paperService) {
        this.paperService = paperService;
    }

    @PostMapping("/admin")
    public Result<PaperEntity> create(@Valid @RequestBody PaperSaveRequest request, HttpServletRequest httpRequest) {
        UserHeaderContext.requireAdmin(httpRequest);
        return Result.success("创建论文成功", paperService.create(request));
    }

    @PutMapping("/admin/{id}")
    public Result<PaperEntity> update(@PathVariable("id") Long id,
                                      @Valid @RequestBody PaperSaveRequest request,
                                      HttpServletRequest httpRequest) {
        UserHeaderContext.requireAdmin(httpRequest);
        return Result.success("更新论文成功", paperService.update(id, request));
    }

    @DeleteMapping("/admin/{id}")
    public Result<Void> delete(@PathVariable("id") Long id, HttpServletRequest httpRequest) {
        UserHeaderContext.requireAdmin(httpRequest);
        paperService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<PaperEntity> detail(@PathVariable("id") Long id) {
        return Result.success(paperService.detail(id, true));
    }

    @GetMapping("/page")
    public Result<IPage<PaperEntity>> page(@RequestParam(value = "current", defaultValue = "1") int current,
                                           @RequestParam(value = "size", defaultValue = "10") int size,
                                           @RequestParam(value = "keyword", required = false) String keyword,
                                           @RequestParam(value = "categoryId", required = false) Long categoryId) {
        return Result.success(paperService.page(current, size, keyword, categoryId));
    }
}
