package org.dromara.omind.baseplat.api.domain.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "QuerySysStationDto", description = "查询充电站请求")
public class QuerySysStationDto {

    @Schema(description = "充电站ID")
    private String stationId;

    @Schema(description = "充电站名称")
    private String stationName;

    @Schema(description = "运营商ID")
    private String operatorId;

    @Schema(description = "设备所有者ID")
    private String equipmentOwnerId;

    @Schema(description = "国家编码")
    private String countryCode;

    @Schema(description = "地区编码")
    private String areaCode;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "站点类型")
    private Integer stationType;

    @Schema(description = "站点状态")
    private Short stationStatus;
}
