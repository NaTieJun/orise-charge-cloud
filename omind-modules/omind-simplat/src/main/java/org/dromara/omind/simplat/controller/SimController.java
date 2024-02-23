package org.dromara.omind.simplat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.domain.R;
import org.dromara.omind.simplat.config.SimCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@Tag(name ="SimController",description = "充电桩模拟器操作接口")
@RestController
@RequestMapping("/evcs/sim/v1")
public class SimController {

    @Autowired
    SimCenter simCenter;

    @Operation(summary = "启动模拟充电桩", method = "GET")
    @GetMapping("/start")
    public R start(String connectorId){
        try {
            log.info("sim-api-start");
            if (TextUtils.isBlank(connectorId)) {
                return R.fail("connectorId无效");
            }
            log.info("sim-api-start-check");
            if (simCenter.isOnline(connectorId)) {
                return R.fail("充电桩已启动，请勿重复启动");
            }
            log.info("sim-api-start-run");
            simCenter.start(connectorId);
        }
        catch (Exception ex){
            log.error("模拟桩启动失败", ex);
            return R.fail("启动失败");
        }
        return R.ok("启动成功");
    }

    @Operation(summary = "停止模拟充电桩", method = "GET")
    @GetMapping("/stop")
    public R stop(String connectorId){
        if(TextUtils.isBlank(connectorId)){
            return R.fail("connectorId无效");
        }
        try {
            simCenter.stop(connectorId);
            return R.ok("停止成功");
        }
        catch (Exception ex){
            log.error("模拟桩停止失败", ex);
            return R.fail("停止失败");
        }
    }

    @Operation(summary = "插枪", method = "GET")
    @GetMapping("/link")
    public R link(String connectorId){
        if(TextUtils.isBlank(connectorId)){
            return R.fail("connectorId无效");
        }
        try{
            simCenter.link(connectorId);
            return R.ok("插枪成功");
        }
        catch (Exception ex){
            log.error("模拟桩插枪失败", ex);
            return R.fail("插枪失败");
        }
    }

    @Operation(summary = "拔枪", method = "GET")
    @GetMapping("/unlink")
    public R unLink(String connectorId){
        if(TextUtils.isBlank(connectorId)){
            return R.fail("connectorId无效");
        }
        try{
            simCenter.unlink(connectorId);
            return R.ok("拔枪成功");
        }
        catch (Exception ex){
            log.error("模拟桩拔枪失败", ex);
            return R.fail("拔枪失败");
        }
    }

}
