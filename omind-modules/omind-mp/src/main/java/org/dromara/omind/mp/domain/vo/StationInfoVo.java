package org.dromara.omind.mp.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.omind.userplat.api.domain.entity.OmindStationEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Schema(description = "充电站详情")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class StationInfoVo implements Serializable {

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

    @Schema(description = "详细地址")
    private String address;

    @Schema(description = "纬度")
    private BigDecimal lat;

    @Schema(description = "经度")
    private BigDecimal lon;

    @Schema(description = "图片列表")
    private String imgs;

    @Schema(description = "快充接口总数")
    private Integer totalFastGun;

    @Schema(description = "快充接口空闲数")
    private Integer freeFastGun;

    @Schema(description = "充电接口总数")
    private Integer totalGun;

    @Schema(description = "充电接口空闲数")
    private Integer freeGun;

    @Schema(description = "桩列表")
    private List<PileVo> pileList;

    @Schema(description = "价格列表")
    private List<PriceVo> priceList;

    @Schema(description = "运维信息")
    private OmindStationEntity attachInfo;

    @Schema(description = "站点电话")
    private String stationPhone;

    @Schema(description = "服务电话")
    private String servicePhone;

    @Schema(description = "站点类型标记 10 自建站")
    private Short platType;

    @Schema(description = "营业时间描述")
    private String busineHours;
}
