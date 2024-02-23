package org.dromara.omind.baseplat.api.domain.notifications;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "推送充电站价格信息Body")
@Data
public class NotificationStationFeeBodyData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "StationFee", description = "充电价格外层")
    @JsonProperty(value = "StationFee")
    NotificationStationFeeData stationFeeData;



}
