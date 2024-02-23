package org.dromara.omind.userplat.api.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "用户车辆审核信息")
public class OmindUserCarCheckDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "车辆id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "审核状态:0、待审核;1、审核通过;2、审核不通过", requiredMode = Schema.RequiredMode.REQUIRED)
    private Short checkState;
}
