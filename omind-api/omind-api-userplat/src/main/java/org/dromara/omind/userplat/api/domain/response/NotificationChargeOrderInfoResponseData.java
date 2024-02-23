package org.dromara.omind.userplat.api.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "推送充电订单信息 返回数据")
@Data
public class NotificationChargeOrderInfoResponseData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "StartChargeSeq", description = "充电订单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "StartChargeSeq")
    private String startChargeSeq;

    @Schema(name = "ConnectorID", description = "充电设备接口编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "ConnectorID")
    private String connectorID;

    @Schema(name = "ConfirmResult", description = "确认结果 0成功 1争议交易 2-99自定义", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "ConfirmResult")
    private Short confirmResult = 99;

}
