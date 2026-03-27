package cn.academic.cloud.user.controller;

import cn.academic.cloud.common.core.Result;
import cn.academic.cloud.common.dto.FavoriteDTO;
import cn.academic.cloud.user.service.FavoriteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user/internal")
public class InternalUserController {

    private final FavoriteService favoriteService;

    public InternalUserController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    /**
     * 提供给ai-service使用的内部接口。
     */
    @GetMapping("/favorites")
    public Result<List<FavoriteDTO>> favorites(@RequestParam("userId") Long userId) {
        return Result.success(favoriteService.listFavoriteDTOByUserId(userId));
    }
}
