package org.dromara.omind.baseplat.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "统计单元")
@Data
public class StatsUnit {

    @Schema(description = "时间戳")
    private Long tm;

    @Schema(description = "值")
    private BigDecimal value;

    @Schema(description = "单位")
    private String unit;

    public StatsUnit(){
        tm = 0L;
        value = new BigDecimal("0.00");
        unit = "";
    }

    public StatsUnit(Long tm, BigDecimal value, String unit) {
        this.tm = tm;
        this.value = value;
        this.unit = unit;
    }
}
