package cn.academic.cloud.user.controller;

import cn.academic.cloud.common.core.Result;
import cn.academic.cloud.user.domain.dto.FavoriteCreateRequest;
import cn.academic.cloud.user.domain.entity.FavoriteEntity;
import cn.academic.cloud.user.service.FavoriteService;
import cn.academic.cloud.user.support.UserHeaderContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping
    public Result<FavoriteEntity> add(@Valid @RequestBody FavoriteCreateRequest request, HttpServletRequest httpRequest) {
        Long userId = UserHeaderContext.getRequiredUserId(httpRequest);
        return Result.success("收藏成功", favoriteService.addFavorite(userId, request));
    }

    @GetMapping("/mine")
    public Result<List<FavoriteEntity>> mine(HttpServletRequest request) {
        Long userId = UserHeaderContext.getRequiredUserId(request);
        return Result.success(favoriteService.listByUserId(userId));
    }

    @DeleteMapping("/{favoriteId}")
    public Result<Void> delete(@PathVariable("favoriteId") Long favoriteId, HttpServletRequest request) {
        Long userId = UserHeaderContext.getRequiredUserId(request);
        favoriteService.deleteFavorite(userId, favoriteId);
        return Result.success();
    }
}
