package org.dromara.omind.baseplat.api.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.omind.baseplat.api.domain.entity.SysChargeOrder;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(name = "SysChargeOrderDto", description = "充电原始订单对象")
public class SysChargeOrderDto extends SysChargeOrder implements Serializable {

    @Schema(description = "用户平台运营商名称")
    private String operatorName;

    @Schema(description = "充电站名称")
    private String stationName;

    @Schema(description = "充电站ID")
    private String stationId;

    @Schema(description = "充电时长（分钟）")
    private Integer durant;

    @Schema(description = "订单状态名")
    private String statName;

    public static SysChargeOrderDto build(SysChargeOrder sysChargeOrder){
        SysChargeOrderDto chargeOrderDto = new SysChargeOrderDto();
        if(sysChargeOrder != null){
            chargeOrderDto.setId(sysChargeOrder.getId());
            chargeOrderDto.setStartChargeSeq(sysChargeOrder.getStartChargeSeq());
            chargeOrderDto.setStartChargeSeqStat(sysChargeOrder.getStartChargeSeqStat());
            chargeOrderDto.setConnectorStatus(sysChargeOrder.getConnectorStatus());
            chargeOrderDto.setOperatorId(sysChargeOrder.getOperatorId());
            chargeOrderDto.setTradeNo(sysChargeOrder.getTradeNo());
            chargeOrderDto.setConnectorId(sysChargeOrder.getConnectorId());
            chargeOrderDto.setCurrentA(sysChargeOrder.getCurrentA());
            chargeOrderDto.setCurrentB(sysChargeOrder.getCurrentB());
            chargeOrderDto.setCurrentC(sysChargeOrder.getCurrentC());
            chargeOrderDto.setVoltageA(sysChargeOrder.getVoltageA());
            chargeOrderDto.setVoltageB(sysChargeOrder.getVoltageB());
            chargeOrderDto.setVoltageC(sysChargeOrder.getVoltageC());
            chargeOrderDto.setSoc(sysChargeOrder.getSoc());
            chargeOrderDto.setStartTime(sysChargeOrder.getStartTime());
            chargeOrderDto.setEndTime(sysChargeOrder.getEndTime());
            chargeOrderDto.setTotalPower(sysChargeOrder.getTotalPower());
            chargeOrderDto.setElecMoney(sysChargeOrder.getElecMoney());
            chargeOrderDto.setServiceMoney(sysChargeOrder.getServiceMoney());
            chargeOrderDto.setTotalMoney(sysChargeOrder.getTotalMoney());
            chargeOrderDto.setSumPeriod(sysChargeOrder.getSumPeriod());
            chargeOrderDto.setFailReason(sysChargeOrder.getFailReason());
            chargeOrderDto.setCarVin(sysChargeOrder.getCarVin());
            chargeOrderDto.setPhoneNum(sysChargeOrder.getPhoneNum());
            chargeOrderDto.setPlateNum(sysChargeOrder.getPlateNum());
            chargeOrderDto.setSyncFlag(sysChargeOrder.getSyncFlag());
            chargeOrderDto.setReportGov(sysChargeOrder.getReportGov());
            chargeOrderDto.setCreateTime(sysChargeOrder.getCreateTime());
            chargeOrderDto.setUpdateTime(sysChargeOrder.getUpdateTime());

            //充电订单状态；1启动中 2充电中 3停止中 4已结束 5未知
            if(sysChargeOrder.getStartChargeSeqStat() == 1){
                chargeOrderDto.setStatName("启动中");
            }
            else if(sysChargeOrder.getStartChargeSeqStat() == 2){
                chargeOrderDto.setStatName("充电中");
            }
            else if(sysChargeOrder.getStartChargeSeqStat() == 3){
                chargeOrderDto.setStatName("停止中");
            }
            else if(sysChargeOrder.getStartChargeSeqStat() == 4){
                chargeOrderDto.setStatName("已结束");
            }
            else{
                chargeOrderDto.setStatName("未知");
            }

            if(sysChargeOrder.getStartTime() != null
                && sysChargeOrder.getEndTime() != null
                && sysChargeOrder.getEndTime().getTime() > sysChargeOrder.getStartTime().getTime())
            {
                int dur = (int)(sysChargeOrder.getEndTime().getTime() - sysChargeOrder.getStartTime().getTime()) / 1000 / 60;
                chargeOrderDto.setDurant(dur);
            }
            else{
                chargeOrderDto.setDurant(0);
            }
        }
        return chargeOrderDto;
    }

}
