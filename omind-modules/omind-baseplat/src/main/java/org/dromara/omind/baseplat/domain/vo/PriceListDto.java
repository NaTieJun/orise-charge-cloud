package org.dromara.omind.baseplat.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "价格信息列表")
@Data
public class PriceListDto {

    @Schema(description = "价格编码")
    Long priceCode;

    @Schema(description = "说明")
    String remark;

    @Schema(description = "创建时间")
    String time;

}
