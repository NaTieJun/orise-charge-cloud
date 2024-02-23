package org.dromara.omind.userplat.api.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.omind.userplat.api.domain.notifications.NotificationEquipChargeStatusData;

import java.io.Serializable;

@Schema(description = "异步推送充电状态信息")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AsynNotificationEquipChargeStatusData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "OperatorID", description = "运营商id", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "OperatorID")
    private String operatorID;

    @Schema(name = "EquipChargeStatusData", description = "充电状态数据信息", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "EquipChargeStatusData")
    private NotificationEquipChargeStatusData equipChargeStatusData;
}
