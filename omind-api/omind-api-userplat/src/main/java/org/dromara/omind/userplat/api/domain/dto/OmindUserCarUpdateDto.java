package org.dromara.omind.userplat.api.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "更新车牌信息")
@Data
public class OmindUserCarUpdateDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "关联ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "用户id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long userId;

    @Schema(description = "车牌号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String plateNo;


}
