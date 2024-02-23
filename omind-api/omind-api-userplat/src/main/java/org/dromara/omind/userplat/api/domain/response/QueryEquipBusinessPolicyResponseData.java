package org.dromara.omind.userplat.api.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.omind.userplat.api.domain.datas.PolicyInfoData;

import java.io.Serializable;
import java.util.List;

@Schema(description = "查询业务策略信息 返回数据")
@Data
public class QueryEquipBusinessPolicyResponseData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "EquipBizSeq", description = "业务策略查询流水号", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "EquipBizSeq")
    private String equipBizSeq;

    @Schema(name = "ConnectorID", description = "充电设备接口编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "ConnectorID")
    private String connectorID;

    @Schema(name = "SuccStat", description = "操作结果，0成功 1失败", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "SuccStat")
    private Short succStat;

    @Schema(name = "FailReason", description = "失败原因，0无 1此充电桩业务策略不存在", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "FailReason")
    private Short failReason;

    @Schema(name = "SumPeriod", description = "时段数N 0-32", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "SumPeriod")
    private Short sumPeriod;

    @Schema(name = "PolicyInfos", description = "计费信息，策略数组", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty(value = "PolicyInfos")
    private List<PolicyInfoData> policyInfos;

}
