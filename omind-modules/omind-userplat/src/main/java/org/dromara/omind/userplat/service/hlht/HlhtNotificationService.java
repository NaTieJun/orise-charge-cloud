package org.dromara.omind.userplat.service.hlht;

import org.dromara.common.core.exception.base.BaseException;
import org.dromara.omind.userplat.api.domain.dto.HlhtDto;
import org.dromara.omind.userplat.api.domain.dto.HlhtResult;
import org.dromara.omind.userplat.api.domain.entity.OmindOperatorEntity;

public interface HlhtNotificationService {

    /**
     * 基础平台推送设备状态变化信息
     * 接口名称：notification_stationStatus
     *
     * @return
     */
    HlhtResult stationStatus(HlhtDto hlhtDto) throws BaseException;

    /**
     * 基础平台推送启动充电结果信息
     * 接口名称：notification_start_charge_result
     *
     * @return
     */
    HlhtResult startChargeResult(HlhtDto hlhtDto);

    /**
     * 基础平台推送充电设备的充电状态
     * 接口名称：notification_equip_charge_status
     *
     * @return
     */
    HlhtResult equipChargeStatus(HlhtDto hlhtDto) throws BaseException;

    /**
     * 基础平台推送停止充电结果
     * 接口名称：notification_stop_charge_result
     *
     * @return
     */
    HlhtResult stopChargeResult(HlhtDto hlhtDto);

    /**
     * 基础平台推送充电订单信息
     * 接口名称：notification_charge_order_info
     *
     * @return
     */
    HlhtResult chargeOrderInfo(HlhtDto hlhtDto) throws BaseException;

    /**
     * 查询业务策略信息结果
     * 接口名称：query_equip_business_policy
     *
     * @return
     */
    HlhtResult queryEquipBusinessPolicy(HlhtDto hlhtDto) throws BaseException;

    /**
     * 推送站点价格信息
     * 接口名称：notification_stationFee
     * 接口说明：当站点价格变化时推送最新价格信息
     * 推送频率：当站点价格变化时推送
     * @param hlhtDto
     * @return
     */
    HlhtResult stationFee(HlhtDto hlhtDto) throws BaseException;

    /**
     * 统一响应方法
     * @param t
     * @param odOperatorInfo
     * @param operatorId
     * @return
     * @param <T>
     */
    <T> HlhtResult notifacationResponse(T t, OmindOperatorEntity odOperatorInfo, String operatorId);
}
