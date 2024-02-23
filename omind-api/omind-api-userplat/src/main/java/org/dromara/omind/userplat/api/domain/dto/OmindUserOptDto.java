package org.dromara.omind.userplat.api.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "用户启用/禁用")
public class OmindUserOptDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @Schema(description = "是否禁用用户:0、启用;1、禁用", requiredMode = Schema.RequiredMode.REQUIRED)
    private Short disableFlag;

}
