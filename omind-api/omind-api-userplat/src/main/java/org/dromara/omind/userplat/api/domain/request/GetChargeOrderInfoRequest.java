package org.dromara.omind.userplat.api.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "获取充电详情请求")
@Data
public class GetChargeOrderInfoRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "充电订单号")
    private String startChargeSeq;

}
