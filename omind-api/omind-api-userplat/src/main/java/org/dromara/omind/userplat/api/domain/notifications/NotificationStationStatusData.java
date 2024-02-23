package org.dromara.omind.userplat.api.domain.notifications;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.omind.userplat.api.domain.datas.ConnectorStatusInfoData;

import java.io.Serializable;

@Schema(description = "设备状态变化推送")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotificationStationStatusData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "ConnectorStatusInfo", description = "充电设备接口状态")
    @JsonProperty(value = "ConnectorStatusInfo")
    private ConnectorStatusInfoData connectorStatusInfo;

}
