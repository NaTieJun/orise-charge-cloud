package org.dromara.omind.baseplat.api.domain.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "QuerySysConnectorDto", description = "查询充电接口请求")
@Data
public class QuerySysConnectorDto {

    @Schema(description = "充电接口ID")
    private String connectorId;

    @Schema(description = "所属充电设备ID")
    private String equipmentId;

    @Schema(description = "充电接口名称")
    private String connectorName;

    @Schema(description = "状态 0离网 1空闲 2占用（未充电）3占用（充电）4占用（预约锁定）255故障")
    private Integer status;

    @Schema(description = "正常标记 0正常 1故障")
    private Short state;

}
