package org.dromara.omind.baseplat.api.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

@Tag(name = "LinkStationOperatorRequest", description = "关联充电站和运营商")
@Data
public class LinkStationOperatorRequest {

    @Schema(description = "运营商ID 9位", requiredMode = Schema.RequiredMode.REQUIRED)
    private String operatorId;

    @Schema(description = "充电站ID ", requiredMode = Schema.RequiredMode.REQUIRED)
    private String stationId;

    @Schema(description = "是否必须上传订单信息")
    private Short isSyncTrade;

    @Schema(description = "是否启用 0不启用（默认） 1启用")
    private Short isEnable;

    @Schema(description = "备注")
    private String remark;

}
