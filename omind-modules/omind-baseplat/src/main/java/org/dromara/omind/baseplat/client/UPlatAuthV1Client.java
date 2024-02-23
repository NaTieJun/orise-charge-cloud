package org.dromara.omind.baseplat.client;

import com.dtflys.forest.annotation.*;
import com.dtflys.forest.converter.json.ForestJacksonConverter;
import org.dromara.omind.baseplat.api.domain.dto.HlhtDto;
import org.dromara.omind.baseplat.api.domain.dto.HlhtResult;

public interface UPlatAuthV1Client {

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
}
