package org.dromara.omind.baseplat.api.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "NotificationChargeOrderInfoResponseData", description = "推送充电订单信息 返回数据")
@Data
public class NotificationChargeOrderInfoResponseData {

    @Schema(name = "StartChargeSeq", description = "充电订单号", required = true)
    @JsonProperty(value = "StartChargeSeq")
    private String startChargeSeq;

    @Schema(name = "ConnectorID", description = "充电设备接口编码", required = true)
    @JsonProperty(value = "ConnectorID")
    private String connectorID;

    @Schema(name = "ConfirmResult", description = "确认结果 0成功 1争议交易 2-99自定义", required = true)
    @JsonProperty(value = "ConfirmResult")
    private Short confirmResult = 99;

}
