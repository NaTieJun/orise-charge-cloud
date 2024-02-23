package org.dromara.omind.simplat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.omind.api.common.utils.encrypt.AESUtils;
import org.dromara.omind.api.common.utils.encrypt.HMacMD5Util;
import org.dromara.omind.baseplat.api.constant.HlhtRet;
import org.dromara.omind.baseplat.api.domain.dto.HlhtDto;
import org.dromara.omind.baseplat.api.domain.dto.HlhtResult;
import org.dromara.omind.baseplat.api.domain.entity.SysChargeOrder;
import org.dromara.omind.baseplat.api.domain.entity.SysConnector;
import org.dromara.omind.baseplat.api.domain.entity.SysEquipment;
import org.dromara.omind.baseplat.api.domain.entity.SysOperator;
import org.dromara.omind.baseplat.api.domain.request.QueryStartChargeData;
import org.dromara.omind.baseplat.api.domain.request.QueryStopChargeData;
import org.dromara.omind.baseplat.api.domain.response.QueryStartChargeResponseData;
import org.dromara.omind.baseplat.api.domain.response.QueryStopChargeResponseData;
import org.dromara.omind.baseplat.api.service.RemoteSysChargeOrderService;
import org.dromara.omind.baseplat.api.service.RemoteSysConnectorService;
import org.dromara.omind.baseplat.api.service.RemoteSysEquipmentService;
import org.dromara.omind.baseplat.api.service.RemoteSysOperatorService;
import org.dromara.omind.baseplat.api.service.pile.RemoteStartChargingService;
import org.dromara.omind.baseplat.api.service.pile.RemoteStopChargingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Log4j2
@Tag(name = "DeviceController", description = "[V1]设备层接口（内部分发）")
@RestController
@RequestMapping("/evcs/v1/device")
public class DeviceController {

    @DubboReference
    RemoteSysOperatorService remoteSysOperatorService;

    @DubboReference
    RemoteSysConnectorService remoteSysConnectorService;

    @DubboReference
    RemoteSysEquipmentService remoteSysEquipmentService;

    @Autowired
    @Lazy
    RemoteStopChargingService remoteStopChargingService;

    @Autowired
    @Lazy
    RemoteStartChargingService remoteStartChargingService;

    @DubboReference
    RemoteSysChargeOrderService remoteSysChargeOrderService;

    @Operation(summary = "[V1]请求启动充电")
    @PostMapping("query_start_charge")
    public HlhtResult queryStartCharge(@RequestBody HlhtDto hlhtDto){

        String operatorId = hlhtDto.getOperatorID();
        SysOperator sysOperator = remoteSysOperatorService.getOperatorById(operatorId);
        QueryStartChargeData queryStartChargeData = hlhtDto.getDataObj(sysOperator,
            QueryStartChargeData.class);

        SysConnector sysConnector = remoteSysConnectorService.getConnectorById(queryStartChargeData.getConnectorID());

        QueryStartChargeResponseData responseData = new QueryStartChargeResponseData();
        responseData.setStartChargeSeq(queryStartChargeData.getStartChargeSeq());
        responseData.setConnectorID(queryStartChargeData.getConnectorID());
        if (sysConnector == null) {
            //未找到充电桩，结束
            responseData.setStartChargeSeqStat((short) 4);
            responseData.setSuccStat((short) 1);
            responseData.setFailReason((short) 1);
        } else if (sysConnector.getStatus() != 2 || sysConnector.getState() != 0) {
            //getStatus状态 0离网 1空闲 2占用（未充电）3占用（充电）4占用（预约锁定）255故障
            //state充电桩状态 0正常 1故障
            responseData.setStartChargeSeqStat((short) 4);
            responseData.setSuccStat((short) 1);
            if (sysConnector.getStatus() == 0
                || sysConnector.getStatus() == 255
                || sysConnector.getState() == 1) {
                responseData.setFailReason((short) 2);
            } else if (sysConnector.getStatus() == 3) {
                responseData.setFailReason((short) 91);  //该车辆充电中
            } else if (sysConnector.getStatus() == 1) {
                responseData.setFailReason((short) 3);   //空闲（未插枪）
            } else if (sysConnector.getStatus() >= 3) {
                responseData.setFailReason((short) 4);   //其他错误
            }
        } else {
            try {
                if (TextUtils.isBlank(queryStartChargeData.getPhoneNum())) {
                    queryStartChargeData.setPhoneNum("13800000000");
                }
                if (queryStartChargeData.getBalance() == null || queryStartChargeData.getBalance().floatValue() <= 0) {
                    queryStartChargeData.setBalance(new BigDecimal("9999"));
                }
                int result = remoteStartChargingService.startCharging(sysOperator, queryStartChargeData);
                if (result != 0) {
                    //创建订单失败
                    responseData.setStartChargeSeqStat((short) 4);
                    responseData.setSuccStat((short) 1);
                    responseData.setFailReason((short) result);
                } else {
                    responseData.setStartChargeSeqStat((short) 1);
                    responseData.setSuccStat((short) 0);
                    responseData.setFailReason((short) 0);
                }
            } catch (BaseException ex) {
                log.error("【远程启机异常】***********");
                log.error(ex.toString());
                responseData.setStartChargeSeqStat((short) 4);
                responseData.setSuccStat((short) 1);
                responseData.setFailReason((short) 99);
            }
        }

        String responseJson = JsonUtils.toJsonString(responseData);
        HlhtResult result = new HlhtResult();
        result.setData(AESUtils.aes128CbcPKCS5Padding(sysOperator.getDataSecret(), sysOperator.getDataSecretIv(), responseJson));
        result.setRet(HlhtRet.OK);
        result.setMsg("");
        result.setSig(HMacMD5Util.getHMacMD5(sysOperator.getSigSecret(), result.getRet() + result.getMsg() + result.getData()));
        return result;
    }


    @Operation(summary = "[V1]请求停止充电")
    @PostMapping("query_stop_charge")
    public HlhtResult queryStopCharge(@RequestBody HlhtDto hlhtDto) {
        String operatorId = hlhtDto.getOperatorID();
        SysOperator sysOperator = remoteSysOperatorService.getOperatorById(operatorId);
        if(sysOperator == null){
            return HlhtResult.error(HlhtRet.ERROR_PARAM, "参数错误-运营商ID");
        }
        HlhtResult ret = hlhtDto.checkValid(sysOperator);
        if(ret.getRet() != HlhtRet.OK){
            return ret;
        }

        QueryStopChargeData queryStopChargeData = hlhtDto.getDataObj(sysOperator,
            QueryStopChargeData.class);

        SysChargeOrder sysChargeOrder = remoteSysChargeOrderService.getChargeOrderByStartChargeSeq(queryStopChargeData.getStartChargeSeq());
        QueryStopChargeResponseData responseData = new QueryStopChargeResponseData();
        SysConnector sysConnector = remoteSysConnectorService.getConnectorById(queryStopChargeData.getConnectorID());
        SysEquipment sysEquipment = remoteSysEquipmentService.getEquipmentById(sysConnector.getEquipmentId());

        responseData.setStartChargeSeq(queryStopChargeData.getStartChargeSeq());
        if (sysChargeOrder == null) {
            responseData.setStartChargeSeqStat((short) 5);
            responseData.setSuccStat((short) 1);
            responseData.setFailReason((short) 4);   //订单不存在
        } else if (sysChargeOrder.getStartChargeSeqStat() == 1 || sysChargeOrder.getStartChargeSeqStat() == 2 || sysChargeOrder.getStartChargeSeqStat() == 3) {
            //正确的逻辑
            int result = remoteStopChargingService.stopCharging(sysOperator, queryStopChargeData);
            if (result == 0) {
                responseData.setStartChargeSeqStat((short) 3);
                responseData.setSuccStat((short) 0);
                responseData.setFailReason((short) 0);
            } else {
                responseData.setStartChargeSeqStat(sysChargeOrder.getStartChargeSeqStat());
                responseData.setSuccStat((short) 1);
                responseData.setFailReason((short) result);
            }

        }//充电订单状态；1启动中 2充电中 3停止中 4已结束 5未知
        else if (sysChargeOrder.getStartChargeSeqStat() == 4) {
            responseData.setStartChargeSeqStat((short) 4);
            responseData.setSuccStat((short) 0);
            responseData.setFailReason((short) 3);
        } else if (sysChargeOrder.getStartChargeSeqStat() == 5) {
            responseData.setStartChargeSeqStat((short) 5);
            responseData.setSuccStat((short) 1);
            responseData.setFailReason(sysChargeOrder.getFailReason().shortValue());
        }

        String responseJson = JsonUtils.toJsonString(responseData);
        HlhtResult result = new HlhtResult();
        result.setData(AESUtils.aes128CbcPKCS5Padding(sysOperator.getDataSecret(), sysOperator.getDataSecretIv(), responseJson));
        result.setRet(HlhtRet.OK);
        result.setMsg("");
        result.setSig(HMacMD5Util.getHMacMD5(sysOperator.getSigSecret(), result.getRet() + result.getMsg() + result.getData()));
        return result;

    }

}
