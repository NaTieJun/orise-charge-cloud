package org.dromara.omind.userplat.client;

import com.dtflys.forest.annotation.*;
import com.dtflys.forest.converter.json.ForestJacksonConverter;
import org.dromara.omind.userplat.api.domain.dto.HlhtDto;
import org.dromara.omind.userplat.api.domain.dto.HlhtResult;


public interface BPlatAuthV1Client {

    /**
     * 获取token（根据返回的有效期定义合理的缓存时间）
     * @param baseUrl
     * @param data
     * @return
     */
    @Post(
            url = "{baseUrl}query_token"
    )
    @BodyType(
            type = "json",
            encoder = ForestJacksonConverter.class
    )
    HlhtResult queryToken(@Var("baseUrl") String baseUrl, @JSONBody HlhtDto data);


    /**
     * 请求设备认证
     * @param baseUrl
     * @param data
     * @return
     */
    @Post(
            url = "{baseUrl}query_equip_auth"
    )
    @BodyType(
            type = "json",
            encoder = ForestJacksonConverter.class
    )
    HlhtResult equipAuth(@Var("baseUrl") String baseUrl, @Header("Authorization") String accessToken, @JSONBody HlhtDto data);

    /**
     * 请求启动充电
     * @param baseUrl
     * @param data
     * @return
     */
    @Post(
            url = "{baseUrl}query_start_charge"
    )
    @BodyType(
            type = "json",
            encoder = ForestJacksonConverter.class
    )
    HlhtResult startCharge(@Var("baseUrl") String baseUrl, @Header("Authorization") String accessToken, @JSONBody HlhtDto data);

    /**
     * 请求停止充电
     * @param baseUrl
     * @param data
     * @return
     */
    @Post(
            url = "{baseUrl}query_stop_charge"
    )
    @BodyType(
            type = "json",
            encoder = ForestJacksonConverter.class
    )
    HlhtResult stopCharge(@Var("baseUrl") String baseUrl, @Header("Authorization") String accessToken, @JSONBody HlhtDto data);


    /**
     * 策略查询
     * @param baseUrl
     * @param data
     * @return
     */
    @Post(
            url = "{baseUrl}query_equip_business_policy"
    )
    @BodyType(
            type = "json",
            encoder = ForestJacksonConverter.class
    )
    HlhtResult equipBusinessPolicyQuery(@Var("baseUrl") String baseUrl, @Header("Authorization") String accessToken, @JSONBody HlhtDto data);

}
