package cn.academic.cloud.user.service;

import cn.academic.cloud.common.dto.FavoriteDTO;
import cn.academic.cloud.user.domain.dto.FavoriteCreateRequest;
import cn.academic.cloud.user.domain.entity.FavoriteEntity;

import java.util.List;

public interface FavoriteService {

    FavoriteEntity addFavorite(Long userId, FavoriteCreateRequest request);

    void deleteFavorite(Long userId, Long favoriteId);

    List<FavoriteEntity> listByUserId(Long userId);

    List<FavoriteDTO> listFavoriteDTOByUserId(Long userId);
}
