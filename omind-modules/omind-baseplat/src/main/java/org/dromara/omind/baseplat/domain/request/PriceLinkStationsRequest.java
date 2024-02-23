package org.dromara.omind.baseplat.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "价格模版关联充电站")
@Data
public class PriceLinkStationsRequest {

    @Schema(description = "价格编码")
    Long priceCode;

    @Schema(description = "充电站ID列表")
    List<String> stationIds;

    @Schema(description = "备注")
    String remark;

}
