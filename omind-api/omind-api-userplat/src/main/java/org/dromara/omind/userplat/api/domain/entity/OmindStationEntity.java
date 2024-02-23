package org.dromara.omind.userplat.api.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Schema(description = "充电站信息")
@Data
@TableName("omind_station")
public class OmindStationEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "id")
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    @Schema(description = "充电站ID")
    @TableField("station_id")
    private String stationId;

    @Schema(description = "运营商ID")
    @TableField("operator_id")
    private String operatorId;

    @Schema(description = "用户平台运营商ID")
    @TableField("user_operator_id")
    private String userOperatorId;

    @Schema(description = "基础平台运营商ID")
    @TableField("base_operator_id")
    private String baseOperatorId;

    @Schema(description = "设备所属商id")
    @TableField("equipment_owner_id")
    private String equipmentOwnerId;

    @Schema(description = "充电站名称")
    @TableField("station_name")
    private String stationName;

    @Schema(description = "充电站国家代码,比如CN")
    @TableField("country_code")
    private String countryCode;

    @Schema(description = "充电站省市辖区编码")
    @TableField("area_code")
    private String areaCode;

    @Schema(description = "详细地址")
    @TableField("address")
    private String address;

    @Schema(description = "站点电话")
    @TableField("station_tel")
    private String stationTel;

    @Schema(description = "服务电话")
    @TableField("service_tel")
    private String serviceTel;

    @Schema(description = "站点类型:1、公共;50、个人;100、公交(专用);101、环卫(专用);102、物流(专用);103、出租车(专用);255、其他")
    @TableField("station_type")
    private Short stationType;

    @Schema(description = "站点状态:0、未知;1、建设中;5、关闭下线;6、维护中;50、正常使用")
    @TableField("station_status")
    private Short stationStatus;

    @Schema(description = "车位数量")
    @TableField("park_nums")
    private Short parkNums;

    @Schema(description = "站点经度")
    @TableField("station_lng")
    private BigDecimal stationLng;

    @Schema(description = "站点纬度")
    @TableField("station_lat")
    private BigDecimal stationLat;

    @Schema(description = "站点引导")
    @TableField("site_guide")
    private String siteGuide;

    @Schema(description = "建设场所类型:1、居民区;2、公共机构;3、企事业单位;4、写字楼;5、工业园区;6、交通枢纽;7、大型文体设施;8、城市绿地;9、大型建筑配建停车场;10、路边停车位;11、城际高速服务区;255、其他")
    @TableField("construction")
    private Short construction;

    @Schema(description = "站点照片JSON串")
    @TableField("pictures")
    private String pictures;

    @Schema(description = "使用车型描述")
    @TableField("match_cars")
    private String matchCars;

    @Schema(description = "车位楼层及数量描述")
    @TableField("park_info")
    private String parkInfo;

    @Schema(description = "营业时间描述")
    @TableField("busine_hours")
    private String busineHours;

    @Schema(description = "充电电费描述")
    @TableField("electricity_fee")
    private String electricityFee;

    @Schema(description = "服务费描述")
    @TableField("service_fee")
    private String serviceFee;

    @Schema(description = "停车费描述")
    @TableField("park_fee")
    private String parkFee;

    @Schema(description = "支付方式::刷卡、线上、现金。其中电子钱包类卡为刷卡，身份鉴权卡、微信/支付宝、APP为线上")
    @TableField("payment")
    private String payment;

    @Schema(description = "是否支持预约")
    @TableField("support_order")
    private Short supportOrder;

    @Schema(description = "平台类型")
    @TableField("plat_type")
    private Short platType;

    @Schema(description = "备注信息")
    @TableField("remark")
    private String remark;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @Schema(description = "删除标志", accessMode = Schema.AccessMode.READ_ONLY)
    @TableField("del_flag")
    @TableLogic
    private Short delFlag;

    /**
     * 创建时间
     **/
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @Schema(description = "设备数量")
    @TableField(exist = false)
    private Integer equipmentCount;

    @Schema(description = "充电枪数量")
    @TableField(exist = false)
    private Integer connectorCount;

    @Schema(description = "运营商名称")
    @TableField(exist = false)
    private String operatorName;
}
