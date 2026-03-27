package cn.academic.cloud.paper.service;

import cn.academic.cloud.paper.domain.dto.PaperCategoryCreateRequest;
import cn.academic.cloud.paper.domain.entity.PaperCategoryEntity;

import java.util.List;

public interface PaperCategoryService {

    PaperCategoryEntity create(PaperCategoryCreateRequest request);

    List<PaperCategoryEntity> listEnabled();

    List<PaperCategoryEntity> listAll();

    PaperCategoryEntity update(Long id, PaperCategoryCreateRequest request);

    void delete(Long id);

    PaperCategoryEntity getById(Long id);
}
