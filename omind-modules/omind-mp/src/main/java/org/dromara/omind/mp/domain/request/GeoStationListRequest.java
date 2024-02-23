package org.dromara.omind.mp.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Schema(description = "地图获取充电站列表")
@Data
public class GeoStationListRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "中心经度", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal lon;

    @Schema(description = "中心纬度", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal lat;

    @Schema(description = "半径（千米），默认50KM")
    private BigDecimal radius;

    @Schema(description = "上限站点数量,默认200")
    private Long limit;

    @Schema(description = "是否显示详情，0否，1是。默认：否")
    private Short isDetail;

    @Schema(description = "排序类型:1、距离从近到远;2、价格从低到高")
    private Short orderType;

    @Schema(description = "用户当前位置经度", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal userLon;

    @Schema(description = "用户当前位置纬度", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal userLat;

}
