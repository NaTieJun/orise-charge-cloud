package org.dromara.omind.userplat.api.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "设备状态变化推送 返回数据")
@Data
public class NotificationStationStatusResponseData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "Status", description = "状态 0接受 1丢弃/忽略，不需要重试")
    @JsonProperty(value = "Status")
    private Short status;

}
