package cn.academic.cloud.paper.service.impl;

import cn.academic.cloud.common.exception.BusinessException;
import cn.academic.cloud.paper.domain.dto.PaperCategoryCreateRequest;
import cn.academic.cloud.paper.domain.entity.PaperCategoryEntity;
import cn.academic.cloud.paper.domain.entity.PaperEntity;
import cn.academic.cloud.paper.mapper.PaperCategoryMapper;
import cn.academic.cloud.paper.mapper.PaperMapper;
import cn.academic.cloud.paper.service.PaperCategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaperCategoryServiceImpl implements PaperCategoryService {

    private final PaperCategoryMapper categoryMapper;
    private final PaperMapper paperMapper;

    public PaperCategoryServiceImpl(PaperCategoryMapper categoryMapper, PaperMapper paperMapper) {
        this.categoryMapper = categoryMapper;
        this.paperMapper = paperMapper;
    }

    @Override
    public PaperCategoryEntity create(PaperCategoryCreateRequest request) {
        ensureNameUnique(request.getName(), null);
        PaperCategoryEntity entity = new PaperCategoryEntity();
        entity.setName(request.getName());
        entity.setSortNo(request.getSortNo() == null ? 0 : request.getSortNo());
        entity.setStatus(request.getStatus() == null ? 1 : request.getStatus());
        entity.setCreateTime(LocalDateTime.now());
        categoryMapper.insert(entity);
        return entity;
    }

    @Override
    public List<PaperCategoryEntity> listEnabled() {
        LambdaQueryWrapper<PaperCategoryEntity> wrapper = new LambdaQueryWrapper<PaperCategoryEntity>()
                .eq(PaperCategoryEntity::getStatus, 1)
                .orderByAsc(PaperCategoryEntity::getSortNo)
                .orderByAsc(PaperCategoryEntity::getId);
        return categoryMapper.selectList(wrapper);
    }

    @Override
    public List<PaperCategoryEntity> listAll() {
        LambdaQueryWrapper<PaperCategoryEntity> wrapper = new LambdaQueryWrapper<PaperCategoryEntity>()
                .orderByAsc(PaperCategoryEntity::getSortNo)
                .orderByAsc(PaperCategoryEntity::getId);
        return categoryMapper.selectList(wrapper);
    }

    @Override
    public PaperCategoryEntity update(Long id, PaperCategoryCreateRequest request) {
        PaperCategoryEntity db = categoryMapper.selectById(id);
        if (db == null) {
            throw new BusinessException("category not found");
        }
        ensureNameUnique(request.getName(), id);
        db.setName(request.getName());
        db.setSortNo(request.getSortNo() == null ? 0 : request.getSortNo());
        db.setStatus(request.getStatus() == null ? db.getStatus() : request.getStatus());
        categoryMapper.updateById(db);
        return db;
    }

    @Override
    public void delete(Long id) {
        long refCount = paperMapper.selectCount(new LambdaQueryWrapper<PaperEntity>()
                .eq(PaperEntity::getCategoryId, id));
        if (refCount > 0) {
            throw new BusinessException("category is referenced by papers and cannot be deleted");
        }
        categoryMapper.deleteById(id);
    }

    @Override
    public PaperCategoryEntity getById(Long id) {
        return categoryMapper.selectById(id);
    }

    private void ensureNameUnique(String name, Long excludeId) {
        LambdaQueryWrapper<PaperCategoryEntity> wrapper = new LambdaQueryWrapper<PaperCategoryEntity>()
                .eq(PaperCategoryEntity::getName, name)
                .last("LIMIT 1");
        if (excludeId != null) {
            wrapper.ne(PaperCategoryEntity::getId, excludeId);
        }
        if (categoryMapper.selectOne(wrapper) != null) {
            throw new BusinessException("category name already exists");
        }
    }
}
