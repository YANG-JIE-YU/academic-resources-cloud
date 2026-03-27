package cn.academic.cloud.ai.client;

import cn.academic.cloud.common.core.Result;
import cn.academic.cloud.common.dto.FavoriteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "academic-cloud-user-service")
public interface UserClient {

    @GetMapping("/api/user/internal/favorites")
    Result<List<FavoriteDTO>> favorites(@RequestParam("userId") Long userId);
}
