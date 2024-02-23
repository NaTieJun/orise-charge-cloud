package org.dromara.omind.mp.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.omind.userplat.api.domain.entity.OmindStationEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Schema(description = "小程序充电站列表返回数据")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class StationVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "索引ID")
    private Long id;

    @Schema(description = "站点ID")
    private String stationId;

    @Schema(description = "站点名称")
    private String stationName;

    @Schema(description = "当前价格")
    private BigDecimal currentPrice;

    @Schema(description = "站点状态:0、未知;1、建设中;5、关闭下线;6、维护中;50、正常使用")
    private Short stationStatus;

    @Schema(description = "纬度")
    private BigDecimal lat;

    @Schema(description = "经度")
    private BigDecimal lon;

    @Schema(description = "距离（KM）")
    private BigDecimal distance;

    @Schema(description = "充电站详情")
    private OmindStationEntity info;

    @Schema(description = "快充接口总数")
    private Integer totalFastGun;

    @Schema(description = "快充接口空闲数")
    private Integer freeFastGun;

    @Schema(description = "充电接口总数")
    private Integer totalGun;

    @Schema(description = "充电接口空闲数")
    private Integer freeGun;

    @Schema(description = "站点图片列表")
    private List<String> imgs;

    @Schema(description = "用户距离（KM）")
    private BigDecimal userDistance;

}
