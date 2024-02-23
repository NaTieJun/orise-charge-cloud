package org.dromara.omind.userplat.api.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "请求设备认证 返回数据")
@Data
public class QueryEquipAuthResponseData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "EquipAuthSeq", description = "设备认证流水号", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "EquipAuthSeq")
    private String equipAuthSeq;

    @Schema(name = "ConnectorID", description = "充电设备接口编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "ConnectorID")
    private String connectorID;

    @Schema(name = "SuccStat", description = "操作结果 0成功 1失败", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "SuccStat")
    private Short succStat;

    @Schema(name = "FailReason", description = "失败原因 0无 1此设备尚未插枪 2设备检测失败 3-99自定义")
    @JsonProperty(value = "FailReason")
    private Short failReason;

    @Schema(name = "FailReasonMsg", description = "失败原因")
    @JsonProperty(value = "FailReasonMsg")
    private String failReasonMsg;

}
