package org.dromara.omind.baseplat.api.domain.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Schema(name = "QuerySysConnectorStatusRecordDto",description = "查询充电接口状态记录请求")
@Data
public class QuerySysConnectorStatusRecordDto {

    @Schema(description = "充电接口ID")
    private String connectorId;

    @Schema(description = "起始-创建时间")
    private Date startTm;

    @Schema(description = "结束-创建时间")
    private Date endTm;

    @Schema(description = "充电订单号")
    private String startChargeSeq;

}
