package org.dromara.omind.userplat.controller.v1.hlht;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.omind.userplat.api.domain.dto.HlhtDto;
import org.dromara.omind.userplat.api.domain.dto.HlhtResult;
import org.dromara.omind.userplat.service.hlht.HlhtNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 推送接口
 */
@Tag(name = "HlhtBusiV1Controller", description = "互联互通业务接口")
@RestController
@RequestMapping(value = "/omind/evcs/v1/")
@Slf4j
public class HlhtBusiV1Controller {

    @Autowired
    @Lazy
    HlhtNotificationService hlhtNotificationService;

    /**
     * 设备状态变化信息推送接口
     * notification_stationStatus
     * @param hlhtDto
     * @return
     */
    @Operation(summary = "设备状态变化信息推送接口")
    @PostMapping(value = "notification_stationStatus")
    public HlhtResult stationStatus(@RequestBody HlhtDto hlhtDto) {
        try {
            return hlhtNotificationService.stationStatus(hlhtDto);
        } catch (BaseException ube) {
            log.error("hlhl-stationStatus-error", ube);
            return HlhtResult.error(ube.getCode(),ube.getMessage());
        } catch (Exception e) {
            log.error("hlhl-stationStatus-error", e);
            return HlhtResult.error("服务器内部错误");
        }
    }

    /**
     * 启动充电结果信息推送接口
     * notification_start_charge_result
     * @param hlhtDto
     * @return
     */
    @Operation(summary = "启动充电结果信息推送接口")
    @PostMapping(value = "notification_start_charge_result")
    public HlhtResult startChargeResult(@RequestBody HlhtDto hlhtDto) {

        try {
            return hlhtNotificationService.startChargeResult(hlhtDto);
        } catch (BaseException ube) {
            log.error("hlhl-startChargeResult-error", ube);
            return HlhtResult.error(ube.getCode(),ube.getMessage());
        } catch (Exception e) {
            log.error("hlhl-startChargeResult-error", e);
            return HlhtResult.error("服务器内部错误");
        }

    }

    /**
     * 充电设备的充电状态推送接口
     * notification_equip_charge_status
     * @param hlhtDto
     * @return
     */
    @Operation(summary = "充电设备的充电状态推送接口")
    @PostMapping(value = "notification_equip_charge_status")
    public HlhtResult equipChargeStatus(@RequestBody HlhtDto hlhtDto) {
        try {
            return hlhtNotificationService.equipChargeStatus(hlhtDto);
        } catch (BaseException ube) {
            log.error("hlhl-equipChargeStatus-error", ube);
            return HlhtResult.error(ube.getCode(),ube.getMessage());
        } catch (Exception e) {
            log.error("hlhl-equipChargeStatus-error", e);
            return HlhtResult.error("服务器内部错误");
        }

    }

    /**
     * 停止充电结果推送接口
     * notification_stop_charge_result
     * @param hlhtDto
     * @return
     */
    @Operation(summary = "停止充电结果推送接口")
    @PostMapping(value = "notification_stop_charge_result")
    public HlhtResult stopChargeResult(@RequestBody HlhtDto hlhtDto) {

        try {
            return hlhtNotificationService.stopChargeResult(hlhtDto);
        } catch (BaseException ube) {
            log.error("hlhl-stopChargeResult-error", ube);
            return HlhtResult.error(ube.getCode(),ube.getMessage());
        } catch (Exception e) {
            log.error("hlhl-stopChargeResult-error", e);
            return HlhtResult.error("服务器内部错误");
        }

    }

    /**
     * 充电订单信息推送接口
     * notification_charge_order_info
     * @param hlhtDto
     * @return
     */
    @Operation(summary = "充电订单信息推送接口")
    @PostMapping(value = "notification_charge_order_info")
    public HlhtResult chargeOrderInfo(@RequestBody HlhtDto hlhtDto) {

        try {
            return hlhtNotificationService.chargeOrderInfo(hlhtDto);
        } catch (BaseException ube) {
            log.error("hlhl-chargeOrderInfo-error", ube);
            return HlhtResult.error(ube.getCode(),ube.getMessage());
        } catch (Exception e) {
            log.error("hlhl-chargeOrderInfo-error", e);
            return HlhtResult.error("服务器内部错误");
        }

    }

    /**
     * 业务策略信息结果查询推送接口
     * query_equip_business_policy
     * @param hlhtDto
     * @return
     */
    @Operation(summary = "业务策略信息结果查询推送接口")
    @PostMapping("query_equip_business_policy")
    public HlhtResult queryEquipBusinessPolicy(@RequestBody HlhtDto hlhtDto){
        try {
            return hlhtNotificationService.queryEquipBusinessPolicy(hlhtDto);
        } catch (BaseException ube) {
            log.error("hlhl-queryEquipBusinessPolicy-error", ube);
            return HlhtResult.error(ube.getCode(),ube.getMessage());
        } catch (Exception e) {
            log.error("hlhl-queryEquipBusinessPolicy-error", e);
            return HlhtResult.error("服务器内部错误");
        }
    }

    /**
     * 站点价格信息推送接口
     * notification_stationFee
     * @param hlhtDto
     * @return
     */
    @Operation(summary = "[OK]站点价格信息推送接口")
    @PostMapping(value = "notification_stationFee")
    public HlhtResult stationFee(@RequestBody HlhtDto hlhtDto) {
        try {
            return hlhtNotificationService.stationFee(hlhtDto);
        } catch (BaseException ube) {
            log.error("hlhl-stationFee-error", ube);
            return HlhtResult.error(ube.getCode(),ube.getMessage());
        } catch (Exception e) {
            log.error("hlhl-stationFee-error", e);
            return HlhtResult.error("服务器内部错误");
        }
    }

}
