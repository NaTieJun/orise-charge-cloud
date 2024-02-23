package org.dromara.omind.baseplat.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Schema(name = "EquipmentStatsInfoData", description = "充电设备统计信息")
@Data
public class EquipmentStatsInfoData {

    @Schema(name = "EquipmentID", description = "设备编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "EquipmentID")
    private String equipmentID;

    @Schema(name = "EquipmentElectricity", description = "充电设备接口累计电量，累计电量，单位为kWh，精度为0.1", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "EquipmentElectricity")
    private BigDecimal equipmentElectricity;

    @Schema(name = "ConnectorStatsInfos", description = "充电设备接口统计信息列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "ConnectorStatsInfos")
    private List<ConnectorStatsInfoData> connectorStatsInfos;

}
