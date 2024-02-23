package org.dromara.omind.baseplat.api.domain.notifications;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "NotificationStartChargeResultData", description = "推送启动充电结果")
@Data
public class NotificationStartChargeResultData {

    @Schema(name = "StartChargeSeq", description = "充电订单号，格式“运营商ID+唯一编号”，27字符")
    @JsonProperty(value = "StartChargeSeq")
    private String startChargeSeq;

    @Schema(name = "StartChargeSeqStat", description = "充电订单状态，1：启动中\n" +
            "\n" +
            "2：充电中\n" +
            "\n" +
            "3：停止中\n" +
            "\n" +
            "4：已结束\n" +
            "\n" +
            "5：未知")
    @JsonProperty(value = "StartChargeSeqStat")
    private Integer startChargeSeqStat;

    @Schema(name = "ConnectorID", description = "充电设备接口编码")
    @JsonProperty(value = "ConnectorID")
    private String connectorID;

    @Schema(name = "StartTime", description = "充电启动时间，格式“yyyy-MM-dd HH:mm:ss”")
    @JsonProperty(value = "StartTime")
    private String startTime;

    @Schema(name = "IdentCode", description = "验证码，该字段作为启动充电失败原因错误码")
    @JsonProperty(value = "IdentCode")
    private String identCode;

}
