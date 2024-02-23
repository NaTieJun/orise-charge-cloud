package org.dromara.omind.userplat.api.domain.notifications;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "基础频台推送站点价格外层信息")
@Data
public class NotificationStationFeeOutData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "StationFee", description = "充电价格外层")
    @JsonProperty(value = "StationFee")
    private NotificationStationFeeData notificationStationFeeData;
}
