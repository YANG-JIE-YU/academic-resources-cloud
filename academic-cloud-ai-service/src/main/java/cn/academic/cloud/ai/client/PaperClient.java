package cn.academic.cloud.ai.client;

import cn.academic.cloud.common.core.Result;
import cn.academic.cloud.common.dto.PaperSimpleDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "academic-cloud-paper-service")
public interface PaperClient {

    @PostMapping("/api/paper/internal/by-ids")
    Result<List<PaperSimpleDTO>> byIds(@RequestBody List<Long> ids);

    @GetMapping("/api/paper/internal/latest")
    Result<List<PaperSimpleDTO>> latest(@RequestParam("limit") int limit);

    @GetMapping("/api/paper/internal/search")
    Result<List<PaperSimpleDTO>> search(@RequestParam(value = "keyword", required = false) String keyword,
                                        @RequestParam("limit") int limit);
}
