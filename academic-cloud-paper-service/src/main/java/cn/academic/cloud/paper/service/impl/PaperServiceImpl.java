package cn.academic.cloud.paper.service.impl;

import cn.academic.cloud.common.dto.PaperSimpleDTO;
import cn.academic.cloud.common.exception.BusinessException;
import cn.academic.cloud.paper.domain.dto.PaperSaveRequest;
import cn.academic.cloud.paper.domain.entity.PaperCategoryEntity;
import cn.academic.cloud.paper.domain.entity.PaperEntity;
import cn.academic.cloud.paper.mapper.PaperMapper;
import cn.academic.cloud.paper.service.PaperCategoryService;
import cn.academic.cloud.paper.service.PaperService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class PaperServiceImpl implements PaperService {

    private final PaperMapper paperMapper;
    private final PaperCategoryService categoryService;

    public PaperServiceImpl(PaperMapper paperMapper, PaperCategoryService categoryService) {
        this.paperMapper = paperMapper;
        this.categoryService = categoryService;
    }

    @Override
    public PaperEntity create(PaperSaveRequest request) {
        validateCategory(request.getCategoryId());

        PaperEntity entity = new PaperEntity();
        copyFromRequest(entity, request);
        entity.setStatus(1);
        entity.setViewCount(0);
        entity.setFavoriteCount(0);
        entity.setCommentCount(0);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        paperMapper.insert(entity);
        return entity;
    }

    @Override
    public PaperEntity update(Long id, PaperSaveRequest request) {
        PaperEntity db = paperMapper.selectById(id);
        if (db == null) {
            throw new BusinessException("论文不存在");
        }
        validateCategory(request.getCategoryId());

        copyFromRequest(db, request);
        db.setUpdateTime(LocalDateTime.now());
        paperMapper.updateById(db);
        return db;
    }

    @Override
    public void delete(Long id) {
        paperMapper.deleteById(id);
    }

    @Override
    public PaperEntity detail(Long id, boolean increaseView) {
        PaperEntity entity = paperMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("论文不存在");
        }
        if (increaseView) {
            Integer viewCount = entity.getViewCount() == null ? 0 : entity.getViewCount();
            entity.setViewCount(viewCount + 1);
            entity.setUpdateTime(LocalDateTime.now());
            paperMapper.updateById(entity);
        }
        return entity;
    }

    @Override
    public IPage<PaperEntity> page(int current, int size, String keyword, Long categoryId) {
        LambdaQueryWrapper<PaperEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(PaperEntity::getTitle, keyword)
                    .or()
                    .like(PaperEntity::getKeywords, keyword);
        }
        if (categoryId != null) {
            wrapper.eq(PaperEntity::getCategoryId, categoryId);
        }
        wrapper.eq(PaperEntity::getStatus, 1);
        wrapper.orderByDesc(PaperEntity::getPublishTime).orderByDesc(PaperEntity::getId);
        return paperMapper.selectPage(new Page<>(current, size), wrapper);
    }

    @Override
    public List<PaperSimpleDTO> listSimpleByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<PaperEntity> wrapper = new LambdaQueryWrapper<PaperEntity>()
                .in(PaperEntity::getId, ids);
        List<PaperEntity> entities = paperMapper.selectList(wrapper);
        return toSimpleDTO(entities);
    }

    @Override
    public List<PaperSimpleDTO> latestSimple(int limit) {
        int validLimit = Math.max(limit, 1);
        LambdaQueryWrapper<PaperEntity> wrapper = new LambdaQueryWrapper<PaperEntity>()
                .eq(PaperEntity::getStatus, 1)
                .orderByDesc(PaperEntity::getPublishTime)
                .orderByDesc(PaperEntity::getId)
                .last("LIMIT " + validLimit);
        return toSimpleDTO(paperMapper.selectList(wrapper));
    }

    @Override
    public List<PaperSimpleDTO> searchSimple(String keyword, int limit) {
        int validLimit = Math.max(limit, 1);
        LambdaQueryWrapper<PaperEntity> wrapper = new LambdaQueryWrapper<PaperEntity>()
                .eq(PaperEntity::getStatus, 1)
                .orderByDesc(PaperEntity::getPublishTime)
                .orderByDesc(PaperEntity::getId)
                .last("LIMIT " + validLimit);
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(PaperEntity::getTitle, keyword)
                    .or()
                    .like(PaperEntity::getKeywords, keyword)
                    .or()
                    .like(PaperEntity::getAbstractText, keyword));
        }
        return toSimpleDTO(paperMapper.selectList(wrapper));
    }

    private void copyFromRequest(PaperEntity entity, PaperSaveRequest request) {
        entity.setTitle(request.getTitle());
        entity.setAbstractText(request.getAbstractText());
        entity.setContent(request.getContent());
        entity.setKeywords(request.getKeywords());
        entity.setAuthorInfo(request.getAuthorInfo());
        entity.setCoverUrl(request.getCoverUrl());
        entity.setCategoryId(request.getCategoryId());
        entity.setPublishTime(request.getPublishTime() == null ? LocalDateTime.now() : request.getPublishTime());
    }

    private void validateCategory(Long categoryId) {
        PaperCategoryEntity category = categoryService.getById(categoryId);
        if (category == null) {
            throw new BusinessException("论文分类不存在");
        }
    }

    private List<PaperSimpleDTO> toSimpleDTO(List<PaperEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyList();
        }
        List<PaperSimpleDTO> list = new ArrayList<>(entities.size());
        for (PaperEntity entity : entities) {
            PaperSimpleDTO dto = new PaperSimpleDTO();
            dto.setId(entity.getId());
            dto.setTitle(entity.getTitle());
            dto.setAbstractText(entity.getAbstractText());
            dto.setKeywords(entity.getKeywords());
            dto.setCategoryId(entity.getCategoryId());
            dto.setPublishTime(entity.getPublishTime());
            dto.setCoverUrl(entity.getCoverUrl());

            PaperCategoryEntity category = categoryService.getById(entity.getCategoryId());
            dto.setCategoryName(category == null ? null : category.getName());
            list.add(dto);
        }
        return list;
    }
}
