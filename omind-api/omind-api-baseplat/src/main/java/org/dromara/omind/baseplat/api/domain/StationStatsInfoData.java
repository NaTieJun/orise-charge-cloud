package org.dromara.omind.baseplat.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Schema(name = "StationStatsInfoData", description = "充电站统计信息")
@Data
public class StationStatsInfoData {

    @Schema(name = "StationID", description = "充电站ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "StationID")
    private String stationID;

    @Schema(name = "StarTime", description = "统计开始时间 格式 yyyy-MM-dd", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "StarTime")
    private String startTime;

    @Schema(name = "EndTime", description = "统计结束时间 格式 yyyy-MM-dd", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "EndTime")
    private String endTime;

    @Schema(name = "StationElectricity", description = "充电站累计电量，单位位kWh，精度位0.1", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "StationElectricity")
    private BigDecimal stationElectricity;

    @Schema(name = "EquipmentStatsInfos", description = "充电设备统计信息列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "EquipmentStatsInfos")
    private List<EquipmentStatsInfoData> equipmentStatsInfos;

}
