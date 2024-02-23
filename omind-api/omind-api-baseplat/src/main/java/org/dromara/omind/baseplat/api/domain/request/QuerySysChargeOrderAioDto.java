package org.dromara.omind.baseplat.api.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Schema(name = "QuerySysChargeOrderAioDto", description = "订单查询对象")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuerySysChargeOrderAioDto {

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

    @Schema(description = "车辆VIN（精准）")
    private String carVin;

    @Schema(description = "车牌号（模糊）")
    private String plateNum;

    @Schema(description = "手机号（精准）")
    private String phoneNum;

    @Schema(description = "订单优惠类型查询:1、全部;2、优惠券;3、企业折扣;4、优惠券或企业折扣")
    private Short couponType;

    @Schema(description = "当前页码")
    private Integer pageNum;

    @Schema(description = "每页显示数量")
    private Integer pageSize;

}
