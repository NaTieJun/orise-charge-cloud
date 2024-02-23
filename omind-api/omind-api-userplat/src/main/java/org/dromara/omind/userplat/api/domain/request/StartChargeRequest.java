package org.dromara.omind.userplat.api.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Schema(description = "充电请求")
@Data
public class StartChargeRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "预设金额", requiredMode = Schema.RequiredMode.AUTO)
    private BigDecimal money;

    @Schema(description = "充电类型 1、充满;2、按金额充电", requiredMode = Schema.RequiredMode.REQUIRED)
    private Short type;

    @Schema(description = "运营商ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String operatorId;

    @Schema(description = "充电设备编码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String connectorId;

    @Schema(description = "车牌号", requiredMode = Schema.RequiredMode.AUTO)
    private String plateNo;

    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.AUTO)
    private String mobile;

}
