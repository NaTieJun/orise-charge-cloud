package org.dromara.omind.baseplat.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "充电桩充电监控概览")
@Data
public class Stats4ConnectorVo {

    @Schema(description = "站点名称")
    public String stationName;

    @Schema(description = "充电枪总数")
    public Integer connectorCount;

    @Schema(description = "空闲数量")
    public Integer freeCount;

    @Schema(description = "插枪数量")
    public Integer unChargeCount;

    @Schema(description = "充电中数量")
    public Integer chargingCount;

    @Schema(description = "离线数量")
    public Integer offlineCount;

    @Schema(description = "故障数量")
    public Integer breakDownCount;

    @Schema(description = "未知原因数量")
    public Integer unkownCount;

    @Schema(description = "充电枪统计列表")
    public List<Stats4ConnectorDataVo> connectorList;

    public Stats4ConnectorVo(){
        this.setStationName("");
        this.connectorCount = 0;
        this.freeCount = 0;
        this.unChargeCount = 0;
        this.chargingCount = 0;
        this.offlineCount = 0;
        this.breakDownCount = 0;
        this.unChargeCount = 0;
        this.unkownCount = 0;
        this.connectorList = new ArrayList<>();
    }

}
