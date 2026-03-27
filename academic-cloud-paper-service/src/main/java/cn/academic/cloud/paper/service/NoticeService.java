package cn.academic.cloud.paper.service;

import cn.academic.cloud.paper.domain.dto.NoticeSaveRequest;
import cn.academic.cloud.paper.domain.entity.NoticeEntity;

import java.util.List;

public interface NoticeService {

    NoticeEntity create(NoticeSaveRequest request);

    NoticeEntity update(Long id, NoticeSaveRequest request);

    void delete(Long id);

    NoticeEntity detail(Long id, boolean adminView);

    List<NoticeEntity> listPublic(String keyword);

    List<NoticeEntity> listAdmin(String keyword);
}
