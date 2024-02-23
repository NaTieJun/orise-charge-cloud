package org.dromara.omind.userplat.api.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Schema(description = "充电枪信息")
@Data
@TableName("omind_equipment")
public class OmindEquipmentEntity  implements Serializable {

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

    @Schema(description = "桩ID")
    @TableField("equipment_id")
    private String equipmentId;

    @Schema(description = "桩名")
    @TableField("equipment_name")
    private String equipmentName;

    @Schema(description = "设备生产商组织机构代码")
    @TableField("manufacturer_id")
    private String manufacturerId;

    @Schema(description = "设备生产商名称")
    @TableField("manufacturer_name")
    private String manufacturerName;

    @Schema(description = "设备型号")
    @TableField("equipment_model")
    private String equipmentModel;

    @Schema(description = "设备生产日期")
    @TableField("production_date")
    private String productionDate;

    @Schema(description = "设备类型:1、直流设备;2、交流设备;3、交直流一体设备;4、无线设备;5、其他")
    @TableField("equipment_type")
    private Short equipmentType;

    @Schema(description = "站点经度")
    @TableField("equipment_lng")
    private BigDecimal equipmentLng;

    @Schema(description = "站点纬度")
    @TableField("equipment_lat")
    private BigDecimal equipmentLat;

    @Schema(description = "额定功率")
    @TableField("power")
    private BigDecimal power;

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

    @Schema(description = "充电枪数量")
    @TableField(exist = false)
    private Integer connectorCount;
}
