package com.kevin.basecore.modules.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.kevin.basecore.common.domin.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_knowledge")
@Schema(description = "AI知识库")
public class AiKnowledge extends BaseEntity {

    @Schema(description = "标题")
    private String title;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "标签")
    private String tags;

    @Schema(description = "状态 1启用 0禁用")
    private Integer status;
}
