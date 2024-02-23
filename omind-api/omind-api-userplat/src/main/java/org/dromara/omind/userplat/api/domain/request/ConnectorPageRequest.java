package org.dromara.omind.userplat.api.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;

@Schema(description = "充电桩分页请求")
@Data
public class ConnectorPageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "充电站id")
    private String stationId;

    @Schema(description = "设备编码")
    private String equipmentId;

}
