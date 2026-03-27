package cn.academic.cloud.paper.service;

import cn.academic.cloud.common.dto.PaperSimpleDTO;
import cn.academic.cloud.paper.domain.dto.PaperSaveRequest;
import cn.academic.cloud.paper.domain.entity.PaperEntity;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public interface PaperService {

    PaperEntity create(PaperSaveRequest request);

    PaperEntity update(Long id, PaperSaveRequest request);

    void delete(Long id);

    PaperEntity detail(Long id, boolean increaseView);

    IPage<PaperEntity> page(int current, int size, String keyword, Long categoryId);

    List<PaperSimpleDTO> listSimpleByIds(List<Long> ids);

    List<PaperSimpleDTO> latestSimple(int limit);

    List<PaperSimpleDTO> searchSimple(String keyword, int limit);
}
