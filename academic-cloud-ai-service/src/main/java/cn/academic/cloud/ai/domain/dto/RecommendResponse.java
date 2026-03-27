package cn.academic.cloud.ai.domain.dto;

import cn.academic.cloud.common.dto.PaperSimpleDTO;

import java.util.List;

public class RecommendResponse {

    private String scene;
    private String trigger;
    private List<PaperSimpleDTO> papers;

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public List<PaperSimpleDTO> getPapers() {
        return papers;
    }

    public void setPapers(List<PaperSimpleDTO> papers) {
        this.papers = papers;
    }
}
