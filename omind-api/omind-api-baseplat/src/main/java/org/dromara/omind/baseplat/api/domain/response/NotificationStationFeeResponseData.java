package org.dromara.omind.baseplat.api.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "NotificationStationFeeResponseData", description = "推送站点价格信息 返回数据")
@Data
public class NotificationStationFeeResponseData {

    @Schema(name = "Status", description = "状态 0接受 1丢弃/忽略，不需要重试")
    @JsonProperty(value = "Status")
    private Short status;
}
