package org.dromara.omind.baseplat.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Schema(description = "价格信息对象")
@Data
public class PriceInfoData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "startTime", description = "0-23 整数", required = true)
    @JsonProperty(value = "startTime")
    private Integer startHour;

    @Schema(name = "elecPrice", description = "时段电费，小数点后4位", required = true)
    @JsonProperty(value = "elecPrice")
    private BigDecimal elecPrice;

    @Schema(name = "servicePrice", description = "时段服务费，小数点后4位", required = true)
    @JsonProperty(value = "servicePrice")
    private BigDecimal servicePrice;

    @Schema(name = "priceType", description = "电费类型（国标外增加）0尖1峰2平3谷", required = true)
    @JsonProperty(value = "priceType")
    private Short priceType;

}
