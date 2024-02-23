package org.dromara.omind.baseplat.client;

import com.dtflys.forest.annotation.*;
import com.dtflys.forest.callback.OnSuccess;
import com.dtflys.forest.converter.json.ForestJacksonConverter;
import org.dromara.omind.baseplat.api.domain.dto.HlhtDto;
import org.dromara.omind.baseplat.api.domain.dto.HlhtResult;

public interface UPlatNotificationV1Client {

    /**
     * 基础平台推送充电订单信息
     * @param baseUrl
     * @param data
     * @return
     */
    @Post(
        url = "{baseUrl}notification_charge_order_info"
    )
    @BodyType(
            type = "json",
            encoder = ForestJacksonConverter.class
    )
    HlhtResult notificationChargeOrderInfo(@Var("baseUrl")String baseUrl, @Var("operatorId") String operatorId, @Header("Authorization") String accessToken, @JSONBody HlhtDto data);

    /**
     * 基础平台推送充电设备的充电状态
     * @param baseUrl
     * @param data
     * @return
     */
    @Post(
        url = "{baseUrl}notification_equip_charge_status",
        async = true
    )
    @BodyType(
            type = "json",
            encoder = ForestJacksonConverter.class
    )
    void notificationEquipChargeStatus(@Var("baseUrl")String baseUrl, @Var("operatorId") String operatorId, @Header("Authorization") String accessToken, @JSONBody HlhtDto data, OnSuccess<HlhtResult> onSuccess);

    /**
     * 基础平台推送启动充电结果信息
     * @param baseUrl
     * @param data
     * @return
     */
    @Post(
        url = "{baseUrl}notification_start_charge_result"
    )
    @BodyType(
            type = "json",
            encoder = ForestJacksonConverter.class
    )
    HlhtResult notificationStartChargeResult(@Var("baseUrl")String baseUrl, @Var("operatorId") String operatorId,@Header("Authorization") String accessToken, @JSONBody HlhtDto data);

    /**
     * 基础平台推送设备状态变化信息
     * @param baseUrl
     * @param data
     * @return
     */
    @Post(
        url = "{baseUrl}notification_stationStatus",
        async = true
    )
    @BodyType(
            type = "json",
            encoder = ForestJacksonConverter.class
    )
    void notificationStationStatus(@Var("baseUrl")String baseUrl, @Var("operatorId") String operatorId,@Header("Authorization") String accessToken, @JSONBody HlhtDto data, OnSuccess<HlhtResult> onSuccess);

    /**
     * 基础平台推送停止充电结果
     * @param baseUrl
     * @param data
     * @return
     */
    @Post(
        url = "{baseUrl}notification_stop_charge_result"
    )
    @BodyType(
            type = "json",
            encoder = ForestJacksonConverter.class
    )
    HlhtResult notificationStopChargeResult(@Var("baseUrl")String baseUrl, @Var("operatorId") String operatorId,@Header("Authorization") String accessToken, @JSONBody HlhtDto data);

    @Post(
            url = "{baseUrl}notification_stationFee",
            async = true
    )
    @BodyType(
            type = "json",
            encoder = ForestJacksonConverter.class
    )
    HlhtResult notificationStationFee(@Var("baseUrl")String baseUrl, @Var("operatorId") String operatorId, @Header("Authorization") String accessToken, @JSONBody HlhtDto data, OnSuccess<HlhtResult> onSuccess);
}
