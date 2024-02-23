package org.dromara.omind.baseplat.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "充电桩充电监控明细")
@Data
public class Stats4ConnectorDataVo {

    @Schema(description = "充电站Id")
    public String stationId;

    @Schema(description = "充电桩Id")
    public String equipmentId;

    @Schema(description = "充电枪Id")
    public String connectorId;

    @Schema(description = "充电枪状态 0、离网;1、空闲;2、占用(未充电);3、占用(充电中);4、占用(预约锁定);255、故障")
    public Short connectorStatus;

    @Schema(description = "电流")
    public BigDecimal currentA;

    @Schema(description = "电压")
    public BigDecimal voltageA;

    @Schema(description = "电池剩余电量(默认:0)")
    public BigDecimal soc;

    @Schema(description = "充电量")
    public BigDecimal chargePower;

    @Schema(description = "充电中的订单号")
    public String startChargeSeq;

    @Schema(description = "统计日充电数量")
    public Integer chargeCount;

    @Schema(description = "统计日总充电量")
    public BigDecimal totalPower;

    @Schema(description = "统计日总充电时长")
    public Integer totalChargeDura;


    public Stats4ConnectorDataVo(){
        this.stationId = "";
        this.equipmentId = "";
        this.connectorId = "";
        this.connectorStatus = 0;
        this.currentA = new BigDecimal("0.00");
        this.voltageA = new BigDecimal("0.00");
        this.soc = new BigDecimal("0.00");
        this.chargePower = new BigDecimal("0.00");
        this.startChargeSeq = "";
        this.chargeCount = 0;
        this.totalPower = new BigDecimal("0.00");
        this.totalChargeDura = 0;
    }

}
