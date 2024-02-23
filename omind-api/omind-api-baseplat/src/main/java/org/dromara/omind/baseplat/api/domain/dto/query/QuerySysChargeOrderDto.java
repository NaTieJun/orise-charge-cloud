package org.dromara.omind.baseplat.api.domain.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Schema(name = "QuerySysChargeOrderDto",description = "查询充电订单请求")
@Data
public class QuerySysChargeOrderDto {

    @Schema(description = "充电接口ID")
    private String connectorId;

    @Schema(description = "运营商ID")
    private String operatorId;

    @Schema(description = "充电站ID")
    private String stationId;

    @Schema(description = "充电订单号")
    private String startChargeSeq;

    @Schema(description = "充电订单状态；1启动中 2充电中 3停止中 4已结束 5未知")
    private Short startChargeSeqStat;

    @Schema(description = "订单创建时间查询起始时间")
    private Date startTm;

    @Schema(description = "订单创建时间查询结束时间")
    private Date endTm;

    @Schema(description = "车VIN")
    private String carVin;

    @Schema(description = "车牌")
    private String plateNum;

    @Schema(description = "手机")
    private String phoneNum;

}
