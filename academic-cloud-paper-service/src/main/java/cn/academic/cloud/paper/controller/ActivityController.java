package cn.academic.cloud.paper.controller;

import cn.academic.cloud.common.core.Result;
import cn.academic.cloud.paper.domain.dto.ActivitySaveRequest;
import cn.academic.cloud.paper.domain.entity.ActivityEntity;
import cn.academic.cloud.paper.service.ActivityService;
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
@RequestMapping("/api/paper/activities")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @PostMapping("/admin")
    public Result<ActivityEntity> create(@Valid @RequestBody ActivitySaveRequest request, HttpServletRequest httpRequest) {
        UserHeaderContext.requireAdmin(httpRequest);
        return Result.success("activity created", activityService.create(request));
    }

    @PutMapping("/admin/{id}")
    public Result<ActivityEntity> update(@PathVariable("id") Long id,
                                         @Valid @RequestBody ActivitySaveRequest request,
                                         HttpServletRequest httpRequest) {
        UserHeaderContext.requireAdmin(httpRequest);
        return Result.success("activity updated", activityService.update(id, request));
    }

    @DeleteMapping("/admin/{id}")
    public Result<Void> delete(@PathVariable("id") Long id, HttpServletRequest httpRequest) {
        UserHeaderContext.requireAdmin(httpRequest);
        activityService.delete(id);
        return Result.success();
    }

    @GetMapping("/admin/list")
    public Result<List<ActivityEntity>> listAdmin(@RequestParam(value = "keyword", required = false) String keyword,
                                                  HttpServletRequest httpRequest) {
        UserHeaderContext.requireAdmin(httpRequest);
        return Result.success(activityService.listAdmin(keyword));
    }

    @GetMapping
    public Result<List<ActivityEntity>> listPublic(@RequestParam(value = "keyword", required = false) String keyword) {
        return Result.success(activityService.listPublic(keyword));
    }

    @GetMapping("/{id}")
    public Result<ActivityEntity> detail(@PathVariable("id") Long id) {
        return Result.success(activityService.detail(id, false));
    }
}
