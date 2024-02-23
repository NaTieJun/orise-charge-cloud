package org.dromara.omind.baseplat.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "StationFeeDetailData", description = "站点价格信息")
@Data
public class StationFeeDetailData {

    @Schema(name = "StartTime", description = "时段起始时间点:时分（例：00:00）")
    @JsonProperty(value = "StartTime")
    private String startTime;

    @Schema(name = "EndTime", description = "时段结束时间点:时分（例：23:59）")
    @JsonProperty(value = "EndTime")
    private String endTime;

    @Schema(name = "ElectricityFee", description = "时段电费:浮点型，小数点后2位")
    @JsonProperty(value = "ElectricityFee")
    private BigDecimal electricityFee;

    @Schema(name = "ServiceFee", description = "时段服务费:浮点型，小数点后2位")
    @JsonProperty(value = "ServiceFee")
    private BigDecimal serviceFee;

    @Schema(name = "EquipmentType", description = "设备类型：1：直流设备2：交流设备3：交直流一体设备")
    @JsonProperty(value = "EquipmentType")
    private Short equipmentType;

}
