package cn.academic.cloud.paper.controller;

import cn.academic.cloud.common.core.Result;
import cn.academic.cloud.paper.domain.dto.PaperCategoryCreateRequest;
import cn.academic.cloud.paper.domain.entity.PaperCategoryEntity;
import cn.academic.cloud.paper.service.PaperCategoryService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/paper/categories")
public class PaperCategoryController {

    private final PaperCategoryService categoryService;

    public PaperCategoryController(PaperCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/admin")
    public Result<PaperCategoryEntity> create(@Valid @RequestBody PaperCategoryCreateRequest request,
                                              HttpServletRequest httpRequest) {
        UserHeaderContext.requireAdmin(httpRequest);
        return Result.success("category created", categoryService.create(request));
    }

    @PutMapping("/admin/{id}")
    public Result<PaperCategoryEntity> update(@PathVariable("id") Long id,
                                              @Valid @RequestBody PaperCategoryCreateRequest request,
                                              HttpServletRequest httpRequest) {
        UserHeaderContext.requireAdmin(httpRequest);
        return Result.success("category updated", categoryService.update(id, request));
    }

    @DeleteMapping("/admin/{id}")
    public Result<Void> delete(@PathVariable("id") Long id, HttpServletRequest httpRequest) {
        UserHeaderContext.requireAdmin(httpRequest);
        categoryService.delete(id);
        return Result.success();
    }

    @GetMapping("/admin/list")
    public Result<List<PaperCategoryEntity>> listAdmin(HttpServletRequest httpRequest) {
        UserHeaderContext.requireAdmin(httpRequest);
        return Result.success(categoryService.listAll());
    }

    @GetMapping
    public Result<List<PaperCategoryEntity>> listEnabled() {
        return Result.success(categoryService.listEnabled());
    }
}
