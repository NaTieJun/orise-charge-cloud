package org.dromara.omind.userplat.api.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Schema(description = "充电站图片信息")
@Data
@TableName("omind_station_images")
public class OmindStationImageEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "id")
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    @Schema(description = "充电站ID")
    @TableField("station_id")
    private String stationId;

    @Schema(description = "图片类型:1、充电站照片;2、充电桩照片;3、充电车位照片;4、停车场入口照片")
    @TableField("image_type")
    private Short imageType;

    @Schema(description = "图片链接")
    @TableField("image_url")
    private String imageUrl;

    @Schema(description = "图片名称")
    @TableField("image_name")
    private String imageName;

    @Schema(description = "显示顺序")
    @TableField("show_seq")
    private Short showSeq;

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

}
