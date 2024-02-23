package org.dromara.omind.baseplat.api.domain.notifications;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "NotificationStopChargeResultData", description = "推送停止充电结果")
@Data
public class NotificationStopChargeResultData {

    @Schema(name = "StartChargeSeq", description = "充电订单号", required = true)
    @JsonProperty(value = "StartChargeSeq")
    private String startChargeSeq;

    @Schema(name = "StartChargeSeqStat", description = "充电订单状态，1启动中 2充电中 3停止中 4已结束 5未知", required = true)
    @JsonProperty(value = "StartChargeSeqStat")
    private Short startChargeSeqStat;

    @Schema(name = "ConnectorID", description = "充电设备接口编码", required = true)
    @JsonProperty(value = "ConnectorID")
    private String connectorID;

    @Schema(name = "SuccStat", description = "成功标识，0成功 1失败", required = true)
    @JsonProperty(value = "SuccStat")
    private Short succStat;

    @Schema(name = "FailReason", description = "停止失败原因,0无 1此设备不存在 2此设备离线 3设备已停止充电 4-99自定义", required = true)
    @JsonProperty(value = "FailReason")
    private Short failReason;

}
