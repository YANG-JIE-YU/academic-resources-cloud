package cn.academic.cloud.paper.domain.dto;

import jakarta.validation.constraints.NotBlank;

public class PaperCategoryCreateRequest {

    @NotBlank(message = "分类名称不能为空")
    private String name;

    private Integer sortNo;

    private Integer status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
