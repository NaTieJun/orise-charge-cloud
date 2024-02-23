package org.dromara.omind.userplat.api.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "充电下单前信息请求")
@Data
public class PrepareOrderInfoRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "充电接口ID")
    private String connectorId;

    @Schema(description = "携带价格列表详情 0 否（默认） 1是")
    private Short hasPrice;

}
