package org.dromara.omind.mp.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


@Schema(description = "时间段价格")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class PriceVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "时间段开始时间 HHmm")
    private String startTm;

    @Schema(description = "时间段结束时间 HHmm")
    private String endTm;

    @Schema(description = "正常时段服务价")
    private BigDecimal servicePrice;

    @Schema(description = "正常时段电价")
    private BigDecimal elecPrice;

    @Schema(description = "备注")
    private String remark;

}
