package org.dromara.omind.baseplat.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Schema(description = "价格类别信息")
@Data
public class PriceTypeInfoData  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "elecPrice", description = "时段电费，小数点后4位", required = true)
    @JsonProperty(value = "elecPrice")
    private BigDecimal elecPrice;

    @Schema(name = "servicePrice", description = "时段服务费，小数点后4位", required = true)
    @JsonProperty(value = "servicePrice")
    private BigDecimal servicePrice;

    @Schema(name = "priceType", description = "电费类型（国标外增加）0尖1峰2平3谷", required = true)
    @JsonProperty(value = "priceType")
    private Short priceType;

    public PriceTypeInfoData(){
        elecPrice = new BigDecimal("0.0000");
        servicePrice = new BigDecimal("0.0000");
        priceType = (short)0;
    }

    public PriceTypeInfoData(Short priceType) {
        elecPrice = new BigDecimal("0.0000");
        servicePrice = new BigDecimal("0.0000");
        this.priceType = priceType;
    }

    public PriceTypeInfoData(BigDecimal elecPrice, BigDecimal servicePrice, Short priceType) {
        this.elecPrice = elecPrice;
        this.servicePrice = servicePrice;
        this.priceType = priceType;
    }
}