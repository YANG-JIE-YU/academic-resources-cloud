package cn.academic.cloud.paper.service;

import cn.academic.cloud.paper.domain.dto.ActivitySaveRequest;
import cn.academic.cloud.paper.domain.entity.ActivityEntity;

import java.util.List;

public interface ActivityService {

    ActivityEntity create(ActivitySaveRequest request);

    ActivityEntity update(Long id, ActivitySaveRequest request);

    void delete(Long id);

    ActivityEntity detail(Long id, boolean adminView);

    List<ActivityEntity> listPublic(String keyword);

    List<ActivityEntity> listAdmin(String keyword);
}
