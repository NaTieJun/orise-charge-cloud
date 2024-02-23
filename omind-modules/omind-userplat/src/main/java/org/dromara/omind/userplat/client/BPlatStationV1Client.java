package org.dromara.omind.userplat.client;

import com.dtflys.forest.annotation.*;
import com.dtflys.forest.converter.json.ForestJacksonConverter;
import org.dromara.omind.userplat.api.domain.dto.HlhtDto;
import org.dromara.omind.userplat.api.domain.dto.HlhtResult;

public interface BPlatStationV1Client {

    /**
     * 用于定期获取每个充电站，在某个周期内的统计信息
     * @param baseUrl
     * @param data
     * @return
     */
    @Post(
            url = "{baseUrl}query_stations_info"
    )
    @BodyType(
            type = "json",
            encoder = ForestJacksonConverter.class
    )
    HlhtResult stationsInfo(@Var("baseUrl") String baseUrl, @Header("Authorization") String accessToken, @JSONBody HlhtDto data);

    /**
     * 查询运营商的充电站的信息
     * @param baseUrl
     * @param data
     * @return
     */
    @Post(
            url = "{baseUrl}query_station_stats"
    )
    @BodyType(
            type = "json",
            encoder = ForestJacksonConverter.class
    )
    HlhtResult stationStats(@Var("baseUrl") String baseUrl, @Header("Authorization") String accessToken, @JSONBody HlhtDto data);

    /**
     * 设备接口状态查询
     * @param baseUrl
     * @param data
     * @return
     */
    @Post(
            url = "{baseUrl}query_station_status"
    )
    @BodyType(
            type = "json",
            encoder = ForestJacksonConverter.class
    )
    HlhtResult stationStatus(@Var("baseUrl") String baseUrl, @Header("Authorization") String accessToken, @JSONBody HlhtDto data);
}
