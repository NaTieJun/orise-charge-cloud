package org.dromara.omind.userplat.api.domain.notifications;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.omind.userplat.api.domain.datas.ChargeDetailData;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Schema(description = "推送充电订单信息")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationChargeOrderInfoData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "StartChargeSeq", description = "充电订单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "StartChargeSeq")
    private String startChargeSeq;

    @Schema(name = "ConnectorID", description = "充电设备接口编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "ConnectorID")
    private String connectorID;

    @Schema(name = "StartTime", description = "开始充电时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "StartTime")
    private String startTime;

    @Schema(name = "EndTime", description = "结束充电时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "EndTime")
    private String endTime;

    @Schema(name = "TotalPower", description = "累计充电量", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "TotalPower")
    private BigDecimal totalPower;

    @Schema(name = "TotalElecMoney", description = "总电费", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "TotalElecMoney")
    private BigDecimal totalElecMoney;

    @Schema(name = "TotalSeviceMoney", description = "总服务费", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "TotalSeviceMoney")
    private BigDecimal totalSeviceMoney;

    @Schema(name = "TotalMoney", description = "累计总金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "TotalMoney")
    private BigDecimal totalMoney;

    @Schema(name = "StopReason", description = "充电结束原因，0:用户手动停止充电" +
            "1:客户归属地运营商平台停止充电\n" +
            "2：BMS停止充电；\n" +
            "3：充电机设备故障；\n" +
            "4：连接器断开\n" +
            "5-99自定义", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "StopReason")
    private Short stopReason;

    @Schema(name = "SubPeriod", description = "时段数N，范围：0～32")
    @JsonProperty(value = "SubPeriod")
    private Short sumPeriod;

    @Schema(name = "ChargeDetails", description = "充电明细信息")
    @JsonProperty(value = "ChargeDetails")
    private List<ChargeDetailData> chargeDetails;

    @Schema(name = "Vin", description = "车辆Vin信息")
    @JsonProperty(value = "Vin")
    private String vin;
}
