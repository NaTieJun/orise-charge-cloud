package org.dromara.omind.simplat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.domain.R;
import org.dromara.omind.baseplat.api.domain.entity.SysChargeOrder;
import org.dromara.omind.baseplat.api.domain.entity.SysOperator;
import org.dromara.omind.baseplat.api.domain.request.QueryStartChargeData;
import org.dromara.omind.baseplat.api.domain.request.QueryStopChargeData;
import org.dromara.omind.baseplat.api.service.RemoteSysChargeOrderService;
import org.dromara.omind.baseplat.api.service.RemoteSysOperatorService;
import org.dromara.omind.baseplat.api.service.pile.RemoteStartChargingService;
import org.dromara.omind.baseplat.api.service.pile.RemoteStopChargingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Log4j2
@Tag(name ="SimTestController",description = "充电桩模拟器操作接口")
@RestController
@RequestMapping("/evcs/sim/test/")
public class SimTestController {

    @DubboReference
    RemoteSysOperatorService remoteSysOperatorService;

    @Autowired
    @Lazy
    RemoteStartChargingService remoteStartChargingService;

    @Autowired
    @Lazy
    RemoteStopChargingService remoteStopChargingService;

    @DubboReference
    RemoteSysChargeOrderService remoteSysChargeOrderService;

    @Operation(summary = "启动一个测试订单", method = "GET")
    @GetMapping("/startChargeTest")
    public R startChargeTest(String connectorId){
        try {
            SysOperator sysOperator = remoteSysOperatorService.getOperatorById("MA01D1QR8");
            QueryStartChargeData queryStartChargeData = new QueryStartChargeData();
            queryStartChargeData.setStartChargeSeq(sysOperator.getOperatorId() + System.currentTimeMillis());
            queryStartChargeData.setConnectorID(connectorId);
            queryStartChargeData.setBalance(new BigDecimal("9999999"));
            queryStartChargeData.setPhoneNum("13910000000");
            queryStartChargeData.setPlateNum("京kz3433");
            queryStartChargeData.setQrCode("");
            int result = remoteStartChargingService.startCharging(sysOperator, queryStartChargeData);
            if(0 == result){
                return R.ok("启动订单成功");
            }
            else{
                return R.ok("启动订单失败" + result);
            }
        }
        catch (Exception ex){
            log.error("启动一个测试订单失败", ex);
            return R.fail("启动一个测试订单失败");
        }

    }

    @Operation(summary = "停止一个测试订单", method = "GET")
    @GetMapping("/stopChargeTest")
    public R stopChargeTest(String startChargeSeq){
        try {
            SysOperator sysOperator = remoteSysOperatorService.getOperatorById("MA01D1QR8");
            SysChargeOrder sysChargeOrder = remoteSysChargeOrderService.getChargeOrderByStartChargeSeq(startChargeSeq);
            if(sysChargeOrder == null){
                return R.fail("停止订单失败(未找到订单)");
            }
            QueryStopChargeData queryStopChargeData = new QueryStopChargeData();
            queryStopChargeData.setConnectorID(sysChargeOrder.getConnectorId());
            queryStopChargeData.setStartChargeSeq(sysChargeOrder.getStartChargeSeq());
            int result = remoteStopChargingService.stopCharging(sysOperator, queryStopChargeData);
            if(0 == result){
                return R.ok("停止订单成功");
            }
            else{
                return R.fail("停止订单失败:" + result);
            }
        }
        catch (Exception ex){
            log.error("停止订单失败", ex);
            return R.fail("停止订单失败");
        }
    }

}
