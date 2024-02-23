package org.dromara.omind.userplat.api.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "推送充电状态 返回数据")
@Data
public class NotificationEquipChargeStatusResponseData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "StartChargeSeq", description = "充电订单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "StartChargeSeq")
    private String startChargeSeq;

    @Schema(name = "SuccStat", description = "操作结果，0成功，1失败", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "SuccStat")
    private Short succStat;

}
