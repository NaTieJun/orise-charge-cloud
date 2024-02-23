package org.dromara.omind.baseplat.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.omind.baseplat.api.domain.entity.SysStation;

@Schema(description = "充电站vo")
@Data
public class StationVo {

    @Schema(description = "站点ID")
    private String stationId;

    @Schema(description = "站点名")
    private String stationName;

    @Schema(description = "区域码")
    private String areaCode;

    @Schema(description = "运营商ID")
    private String operatorId;

    @Schema(description = "站点状态：0、未知;1、建设中;5、关闭下线;6、维护中;50、正常使用")
    private Short stationStatus;

    public static StationVo build(SysStation station){
        StationVo stationVo = new StationVo();
        if(station != null){
            stationVo.setStationId(station.getStationId());
            stationVo.setStationName(station.getStationName());
            stationVo.setAreaCode(station.getAreaCode());
            stationVo.setOperatorId(station.getOperatorId());
            stationVo.setStationStatus(station.getStationStatus());
        }
        return stationVo;
    }
}
