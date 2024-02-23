package org.dromara.omind.baseplat.api.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

@Tag(name = "LinkStationOperatorUpdateRequest", description = "关联充电站和运营商对象更新请求")
@Data
public class LinkStationOperatorUpdateRequest {

    @Schema(description = "修改对象ID")
    private Long id;

    @Schema(description = "0否 1是")
    private Short isSyncTrade;

    @Schema(description = "启用状态 0不启用 1启用")
    private Short isEnable;

    @Schema(description = "备注", example = "")
    private String remark;

}
