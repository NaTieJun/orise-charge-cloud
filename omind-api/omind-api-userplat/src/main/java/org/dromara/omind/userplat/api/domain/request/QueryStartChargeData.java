package org.dromara.omind.userplat.api.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Schema(description = "请求启动充电(附加手机号)")
@Data
public class QueryStartChargeData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "StartChargeSeq", description = "充电订单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "StartChargeSeq")
    private String startChargeSeq;

    @Schema(name = "ConnectorID", description = "充电设备接口编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "ConnectorID")
    private String connectorID;

    @Schema(name = "QrCode", description = "二维码其他信息", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "QrCode")
    private String qrCode;

    @Schema(name = "PhoneNum", description = "手机号")
    @JsonProperty(value = "PhoneNum")
    private String phoneNum;

    @Schema(name = "Balance", description = "充电金额")
    @JsonProperty(value = "Balance")
    private BigDecimal balance;

    @Schema(name = "PlateNum", description = "车牌号")
    @JsonProperty(value = "PlateNum")
    private String plateNum;

}
