package org.dromara.omind.userplat.api.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "用户反馈信息列表查询")
@Data
public class OmindFeedbackListDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户名")
    private String nickName;

    @Schema(description = "手机号")
    private String mobile;
}
