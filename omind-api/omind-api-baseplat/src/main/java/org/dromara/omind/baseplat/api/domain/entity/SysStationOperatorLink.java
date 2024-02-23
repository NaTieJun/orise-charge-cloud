package org.dromara.omind.baseplat.api.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("sys_station_operator_link")
public class SysStationOperatorLink implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    @TableField("station_id")
    private String stationId;

    @TableField("operator_id")
    private String operatorId;

    @TableField("is_sync_trade")
    private Short isSyncTrade;

    @TableField("is_enable")
    private Short isEnable;

    @TableField("remark")
    private String remark;

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

    @Schema(description = "站点数据")
    @TableField(exist = false)
    private SysStation stationInfo;

    @Schema(description = "运营商数据")
    @TableField(exist = false)
    private SysOperator operatorInfo;

}
