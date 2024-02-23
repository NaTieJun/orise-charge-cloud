package org.dromara.omind.baseplat.api.domain.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "QuerySysEquipmentDto", description = "充电桩查询请求")
@Data
public class QuerySysEquipmentDto {

    @Schema(description = "充电设备ID")
    private String equipmentId;

    @Schema(description = "充电桩编号")
    private String pileNo;

    @Schema(description = "充电站ID")
    private String stationId;

    @Schema(description = "充电设备类型 1直流设备 2交流设备 3交直流一体设备 4无线设备 5其他")
    private Short equipmentType;

}
