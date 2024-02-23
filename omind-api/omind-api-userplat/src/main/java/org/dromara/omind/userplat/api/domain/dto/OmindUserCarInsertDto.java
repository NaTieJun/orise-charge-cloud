package org.dromara.omind.userplat.api.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "插入用户车辆")
@Data
public class OmindUserCarInsertDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long userId;

    @Schema(description = "充电订单号", requiredMode = Schema.RequiredMode.AUTO)
    private String startChargeSeq;

    @Schema(description = "车牌号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String plateNo;

}
