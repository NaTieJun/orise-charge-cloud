package org.dromara.omind.baseplat.api.domain.notifications;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.omind.baseplat.api.domain.ConnectorStatusInfoData;

import java.io.Serializable;

@Schema(name = "NotificationStationStatusData", description = "设备状态变化推送")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotificationStationStatusData implements Serializable {

    @Schema(name = "ConnectorStatusInfo", description = "充电设备接口状态")
    @JsonProperty(value = "ConnectorStatusInfo")
    private ConnectorStatusInfoData connectorStatusInfo;

}
