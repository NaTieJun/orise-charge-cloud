package org.dromara.omind.baseplat.api.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("sys_equipment")
public class SysEquipment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    @TableField("equipment_id")
    private String equipmentId;

    @TableField("pile_no")
    private String pileNo;

    @TableField("station_id")
    private String stationId;

    @TableField("price_code")
    private Integer priceCode;

    @TableField("manufacturer_id")
    private String manufacturerId;

    @TableField("manufacturer_name")
    private String manufacturerName;

    @TableField("equipment_model")
    private String equipmentModel;

    @TableField("production_date")
    private String productionDate;

    @TableField("equipment_type")
    private Short equipmentType;

    @TableField("equipment_lng")
    private BigDecimal equipmentLng;

    @TableField("equipment_lat")
    private BigDecimal equipmentLat;

    @TableField("power")
    private BigDecimal power;

    @TableField("max_power")
    private BigDecimal maxPower;

    @TableField("is_working")
    private Short isWorking;

    @TableField("sync_tm")
    private Date syncTm;

    @TableField("online_tm")
    private Date onlineTm;

    @TableField("serv_ip")
    private String serverIp;

    @TableField("equipment_name")
    private String equipmentName;

    @TableField("net_type")
    private Short netType;

    @TableField("m_operator")
    private Short mOperator;

    @TableField(value = "create_by_id", fill = FieldFill.INSERT)
    private Long createById;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "update_by_id", fill = FieldFill.INSERT_UPDATE)
    private Long updateById;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @Schema(description = "删除标志", accessMode = Schema.AccessMode.READ_ONLY)
    @TableField("del_flag")
    @TableLogic
    private Short delFlag;

    @TableField(exist = false)
    private Integer gunNum;

}
