package org.dromara.omind.userplat.api.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "充电订单列表")
@Data
public class ChargeOrderListRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "仅充电中的订单, 0否 1是")
    private Short isOnlyCharing;

}
