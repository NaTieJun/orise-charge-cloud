package org.dromara.omind.userplat.client;

import com.dtflys.forest.annotation.*;
import com.dtflys.forest.converter.json.ForestJacksonConverter;
import org.dromara.omind.userplat.api.domain.dto.HlhtDto;
import org.dromara.omind.userplat.api.domain.dto.HlhtResult;

public interface BPlatChargeV1Client {

    /**
     * 查询充电状态
     * @param baseUrl
     * @param data
     * @return
     */
    @Post(
            url = "{baseUrl}query_equip_charge_status"
    )
    @BodyType(
            type = "json",
            encoder = ForestJacksonConverter.class
    )
    HlhtResult equipChargeStatus(@Var("baseUrl") String baseUrl, @Header("Authorization") String accessToken, @JSONBody HlhtDto data);
}
