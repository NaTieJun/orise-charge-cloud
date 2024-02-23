package org.dromara.omind.baseplat.api.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "QueryStartChargeResponseData", description = "请求启动充电 返回数据")
@Data
public class QueryStartChargeResponseData {

    @Schema(name = "StartChargeSeq", description = "充电订单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "StartChargeSeq")
    private String startChargeSeq;

    @Schema(name = "StartChargeSeqStat", description = "充电订单状态，1启动中，2充电中，3停止中，4已结束，5未知", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "StartChargeSeqStat")
    private Short startChargeSeqStat;

    @Schema(name = "ConnectorID", description = "充电设备接口编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "ConnectorID")
    private String connectorID;

    @Schema(name = "SuccStat", description = "操作结果 0成功 1失败", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "SuccStat")
    private Short succStat;

    @Schema(name = "FailReason", description = "失败原因 0无 1此设备不存在 2此设备离线 3-99自定义 90车辆未备案 91该车辆充电中", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "FailReason")
    private Short failReason;

}
