package org.dromara.omind.baseplat.client;

import com.dtflys.forest.annotation.*;
import com.dtflys.forest.converter.json.ForestJacksonConverter;
import org.dromara.common.core.domain.R;
import org.dromara.omind.baseplat.api.domain.dto.HlhtDto;
import org.dromara.omind.baseplat.api.domain.dto.HlhtResult;
import org.dromara.omind.baseplat.api.domain.request.BaseIdRequest;

public interface UTcpInnerV1Client {

    @Post(
        url="http://{baseUrl}:9804/evcs/v1/device/query_start_charge"
    )
    @BodyType(
            type = "json",
            encoder = ForestJacksonConverter.class
    )
    HlhtResult queryStartCharge(@Var("baseUrl") String baseUrl, @JSONBody HlhtDto data);

    @Post(
        url="http://{baseUrl}:9804/evcs/v1/device/query_stop_charge"
    )
    @BodyType(
            type = "json",
            encoder = ForestJacksonConverter.class
    )
    HlhtResult queryStopCharge(@Var("baseUrl") String baseUrl, @JSONBody HlhtDto data);

    @Post(
        url="http://{baseUrl}:9804/evcs/tcp/v1/billModelSend/{equipmentId}"
    )
    @BodyType(
            type = "json",
            encoder = ForestJacksonConverter.class
    )
    R sendPrice(@Var("baseUrl") String baseUrl, @Var("equipmentId") String equipmentId);


}
