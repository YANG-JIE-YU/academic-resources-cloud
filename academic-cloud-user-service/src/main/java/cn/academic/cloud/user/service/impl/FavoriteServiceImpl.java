package cn.academic.cloud.user.service.impl;

import cn.academic.cloud.common.dto.FavoriteDTO;
import cn.academic.cloud.common.exception.BusinessException;
import cn.academic.cloud.user.domain.dto.FavoriteCreateRequest;
import cn.academic.cloud.user.domain.entity.FavoriteEntity;
import cn.academic.cloud.user.mapper.FavoriteMapper;
import cn.academic.cloud.user.service.FavoriteService;
import cn.academic.cloud.user.service.UserBehaviorService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final UserBehaviorService userBehaviorService;

    public FavoriteServiceImpl(FavoriteMapper favoriteMapper, UserBehaviorService userBehaviorService) {
        this.favoriteMapper = favoriteMapper;
        this.userBehaviorService = userBehaviorService;
    }

    @Override
    public FavoriteEntity addFavorite(Long userId, FavoriteCreateRequest request) {
        LambdaQueryWrapper<FavoriteEntity> existsWrapper = new LambdaQueryWrapper<FavoriteEntity>()
                .eq(FavoriteEntity::getUserId, userId)
                .eq(FavoriteEntity::getTargetType, request.getTargetType())
                .eq(FavoriteEntity::getTargetId, request.getTargetId());
        FavoriteEntity exists = favoriteMapper.selectOne(existsWrapper);
        if (exists != null) {
            return exists;
        }

        FavoriteEntity entity = new FavoriteEntity();
        entity.setUserId(userId);
        entity.setTargetType(request.getTargetType());
        entity.setTargetId(request.getTargetId());
        entity.setTargetTitle(request.getTargetTitle());
        entity.setTargetCover(request.getTargetCover());
        entity.setCreateTime(LocalDateTime.now());
        favoriteMapper.insert(entity);

        userBehaviorService.recordBehavior(userId, "FAVORITE", request.getTargetType(), request.getTargetId(), request.getTargetTitle());
        return entity;
    }

    @Override
    public void deleteFavorite(Long userId, Long favoriteId) {
        FavoriteEntity entity = favoriteMapper.selectById(favoriteId);
        if (entity == null) {
            return;
        }
        if (!userId.equals(entity.getUserId())) {
            throw new BusinessException(403, "无权删除他人收藏记录");
        }
        favoriteMapper.deleteById(favoriteId);
    }

    @Override
    public List<FavoriteEntity> listByUserId(Long userId) {
        LambdaQueryWrapper<FavoriteEntity> wrapper = new LambdaQueryWrapper<FavoriteEntity>()
                .eq(FavoriteEntity::getUserId, userId)
                .orderByDesc(FavoriteEntity::getCreateTime);
        return favoriteMapper.selectList(wrapper);
    }

    @Override
    public List<FavoriteDTO> listFavoriteDTOByUserId(Long userId) {
        List<FavoriteEntity> entities = listByUserId(userId);
        List<FavoriteDTO> list = new ArrayList<>(entities.size());
        for (FavoriteEntity entity : entities) {
            FavoriteDTO dto = new FavoriteDTO();
            dto.setUserId(entity.getUserId());
            dto.setTargetType(entity.getTargetType());
            dto.setTargetId(entity.getTargetId());
            dto.setTargetTitle(entity.getTargetTitle());
            dto.setTargetCover(entity.getTargetCover());
            dto.setCreateTime(entity.getCreateTime());
            list.add(dto);
        }
        return list;
    }
}
