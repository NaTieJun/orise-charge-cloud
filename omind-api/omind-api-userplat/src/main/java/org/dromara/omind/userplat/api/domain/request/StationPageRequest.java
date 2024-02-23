package org.dromara.omind.userplat.api.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "充电站分页请求")
@Data
public class StationPageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "充电站名称", requiredMode = Schema.RequiredMode.AUTO)
    private String stationName;

    @Schema(description = "充电站省市辖区编码", requiredMode = Schema.RequiredMode.AUTO)
    private String areaCode;

    @Schema(description = "运营商id", requiredMode = Schema.RequiredMode.AUTO)
    private String operatorId;

    @Schema(description = "站点id", requiredMode = Schema.RequiredMode.AUTO)
    private String stationId;

    @Schema(description = "站点状态:0、未知;1、建设中;5、关闭下线;6、维护中;50、正常使用", requiredMode = Schema.RequiredMode.AUTO)
    private Short stationStatus;

}
