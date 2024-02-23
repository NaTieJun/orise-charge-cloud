package org.dromara.omind.baseplat.controller.test;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.omind.baseplat.api.domain.entity.SysOperator;
import org.dromara.omind.baseplat.api.domain.request.QueryStartChargeData;
import org.dromara.omind.baseplat.api.domain.request.QueryStopChargeData;
import org.dromara.omind.baseplat.api.service.pile.RemoteStartChargingService;
import org.dromara.omind.baseplat.api.service.pile.RemoteStopChargingService;
import org.dromara.omind.baseplat.service.SysOperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "TestController", description = "[TEST]测试接口")
@RestController
@RequestMapping("/hlht/v1/test/")
public class TestController {

    @DubboReference
    RemoteStartChargingService remoteStartChargingService;

    @DubboReference
    RemoteStopChargingService remoteStopChargingService;

    @Autowired
    SysOperatorService operatorService;


    @PostMapping(value = "startCharge")
    @Operation(summary = "启动充电")
    public R startCharge(@RequestBody QueryStartChargeData queryStartChargeData)
    {
        try {
            String operatorId = "MA01D1QR8";
            SysOperator sysOperator = operatorService.getOperatorById(operatorId);
            int result = remoteStartChargingService.startCharging(sysOperator, queryStartChargeData);
            return R.ok(result);
        }
        catch (BaseException ex){
            return R.fail(ex.toString());
        }
        catch (Exception ex){
            log.error(ex.toString(), ex);
            return R.fail(ex.toString());
        }
    }

    @PostMapping(value = "stopCharge")
    @Operation(summary = "停止充电")
    public R stopCharge(@RequestBody QueryStopChargeData queryStopChargeData)
    {
        try{
            String operatorId = "MA01D1QR8";
            SysOperator sysOperator = operatorService.getOperatorById(operatorId);
            int result = remoteStopChargingService.stopCharging(sysOperator, queryStopChargeData);
            return R.ok(result);
        }
        catch (BaseException ex){
            return R.fail(ex.toString());
        }
        catch (Exception ex){
            log.error(ex.toString(), ex);
            return R.fail(ex.toString());
        }
    }

}
