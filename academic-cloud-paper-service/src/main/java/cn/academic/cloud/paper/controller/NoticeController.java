package cn.academic.cloud.paper.controller;

import cn.academic.cloud.common.core.Result;
import cn.academic.cloud.paper.domain.dto.NoticeSaveRequest;
import cn.academic.cloud.paper.domain.entity.NoticeEntity;
import cn.academic.cloud.paper.service.NoticeService;
import cn.academic.cloud.paper.support.UserHeaderContext;
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

import java.util.List;

@RestController
@RequestMapping("/api/paper/notices")
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @PostMapping("/admin")
    public Result<NoticeEntity> create(@Valid @RequestBody NoticeSaveRequest request, HttpServletRequest httpRequest) {
        UserHeaderContext.requireAdmin(httpRequest);
        return Result.success("notice created", noticeService.create(request));
    }

    @PutMapping("/admin/{id}")
    public Result<NoticeEntity> update(@PathVariable("id") Long id,
                                       @Valid @RequestBody NoticeSaveRequest request,
                                       HttpServletRequest httpRequest) {
        UserHeaderContext.requireAdmin(httpRequest);
        return Result.success("notice updated", noticeService.update(id, request));
    }

    @DeleteMapping("/admin/{id}")
    public Result<Void> delete(@PathVariable("id") Long id, HttpServletRequest httpRequest) {
        UserHeaderContext.requireAdmin(httpRequest);
        noticeService.delete(id);
        return Result.success();
    }

    @GetMapping("/admin/list")
    public Result<List<NoticeEntity>> listAdmin(@RequestParam(value = "keyword", required = false) String keyword,
                                                HttpServletRequest httpRequest) {
        UserHeaderContext.requireAdmin(httpRequest);
        return Result.success(noticeService.listAdmin(keyword));
    }

    @GetMapping
    public Result<List<NoticeEntity>> listPublic(@RequestParam(value = "keyword", required = false) String keyword) {
        return Result.success(noticeService.listPublic(keyword));
    }

    @GetMapping("/{id}")
    public Result<NoticeEntity> detail(@PathVariable("id") Long id) {
        return Result.success(noticeService.detail(id, false));
    }
}
