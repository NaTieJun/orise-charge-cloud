package org.dromara.omind.baseplat.api.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "NotificationStationStatusResponseData", description = "设备状态变化推送 返回数据")
@Data
public class NotificationStationStatusResponseData {

    @Schema(name = "Status", description = "状态 0接受 1丢弃/忽略，不需要重试")
    @JsonProperty(value = "Status")
    private Short status;

}
