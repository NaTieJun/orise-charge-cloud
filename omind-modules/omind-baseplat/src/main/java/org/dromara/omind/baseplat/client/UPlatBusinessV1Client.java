package org.dromara.omind.baseplat.client;

import com.dtflys.forest.annotation.*;
import com.dtflys.forest.converter.json.ForestJacksonConverter;
import org.dromara.omind.baseplat.api.domain.dto.HlhtDto;
import org.dromara.omind.baseplat.api.domain.dto.HlhtResult;

public interface UPlatBusinessV1Client {

    /**
     * 查询业务策略信息
     * @param baseUrl
     * @param data
     * @return
     */
    @Post(
        url="{baseUrl}query_equip_business_policy"
    )
    @BodyType(
            type = "json",
            encoder = ForestJacksonConverter.class
    )
    HlhtResult queryEquipBusinessPolicy(@Var("baseUrl") String baseUrl, @Var("operatorId") String operatorId, @Header("Authorization") String accessToken, @JSONBody HlhtDto data);

    @Post(
        url="{baseUrl}query_stations_info"
    )
    @BodyType(
            type = "json",
            encoder = ForestJacksonConverter.class
    )
    HlhtResult queryStationsInfo(@Var("baseUrl") String baseUrl, @Var("operatorId") String operatorId, @Header("Authorization") String accessToken, @JSONBody HlhtDto data);

    @Post(
        url="{baseUrl}query_station_stats"
    )
    @BodyType(
            type = "json",
            encoder = ForestJacksonConverter.class
    )
    HlhtResult queryStationStats(@Var("baseUrl") String baseUrl, @Var("operatorId") String operatorId, @Header("Authorization") String accessToken, @JSONBody HlhtDto data);


    @Post(
        url="{baseUrl}query_station_status"
    )
    @BodyType(
            type = "json",
            encoder = ForestJacksonConverter.class
    )
    HlhtResult queryStationStatus(@Var("baseUrl") String baseUrl, @Var("operatorId") String operatorId, @Header("Authorization") String accessToken, @JSONBody HlhtDto data);

    /**
     * 查询充电订单业务
     * @param baseUrl
     * @param operatorId
     * @param accessToken
     * @param data
     * @return
     */
    @Post(
        url="{baseUrl}query_equip_charge_status"
    )
    @BodyType(
            type = "json",
            encoder = ForestJacksonConverter.class
    )
    HlhtResult queryEquipChargeStatus(@Var("baseUrl") String baseUrl, @Var("operatorId") String operatorId, @Header("Authorization") String accessToken, @JSONBody HlhtDto data);

}
