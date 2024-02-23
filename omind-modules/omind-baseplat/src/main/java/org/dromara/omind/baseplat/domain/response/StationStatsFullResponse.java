package org.dromara.omind.baseplat.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.omind.baseplat.domain.vo.StatsUnit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "站点全量统计响应")
@Data
public class StationStatsFullResponse {

    @Schema(description = "充电电量")
    private BigDecimal totalPower;

    @Schema(description = "订单总额")
    private BigDecimal totalMoney;

    @Schema(description = "电费总额")
    private BigDecimal totalEMoney;

    @Schema(description = "服务费总额")
    private BigDecimal totalSMoney;

    @Schema(description = "订单数")
    private Integer orderCount;

    @Schema(description = "平均客单价")
    private BigDecimal avo;

    @Schema(description = "充电时长")
    private BigDecimal totalDurant;

    @Schema(description = "总用户数（去重）")
    private Integer totalUserCount;

    @Schema(description = "充电金额曲线图")
    private List<StatsUnit> chargeMoneyList;

    @Schema(description = "服务费金额曲线图")
    private List<StatsUnit> serMoneyList;

    @Schema(description = "电费金额曲线图")
    private List<StatsUnit> eleMoneyList;

    @Schema(description = "充电次数曲线图")
    private List<StatsUnit> chargeCountList;

    @Schema(description = "充电度数曲线图")
    private List<StatsUnit> chargePowerList;

    @Schema(description = "充电金额分布图")
    private List<StatsUnit> chargeMoneyPieList;

    @Schema(description = "充电时长")
    private List<StatsUnit> chargeDurantList;


    public StationStatsFullResponse() {
        totalPower = new BigDecimal("0.0000");
        totalMoney = new BigDecimal("0.0000");
        totalEMoney = new BigDecimal("0.0000");
        totalSMoney = new BigDecimal("0.0000");
        orderCount = 0;
        avo = new BigDecimal("0.0000");
        totalDurant = new BigDecimal("0");
        totalUserCount = 0;
        chargeMoneyList = new ArrayList<>();
        serMoneyList = new ArrayList<>();
        eleMoneyList = new ArrayList<>();
        chargeCountList = new ArrayList<>();
        chargePowerList = new ArrayList<>();
        chargeMoneyPieList = new ArrayList<>();
        chargeDurantList = new ArrayList<>();
    }
}
