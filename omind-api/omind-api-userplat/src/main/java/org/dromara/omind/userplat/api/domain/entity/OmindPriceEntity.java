package org.dromara.omind.userplat.api.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

@Schema(description = "充电价格信息")
@Data
@TableName("omind_price")
public class OmindPriceEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "id")
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    @Schema(description = "充电站ID")
    @TableField("station_id")
    private String stationId;

    @Schema(description = "价格模版Code")
    @TableField("price_code")
    private Long priceCode;

    @Schema(description = "开始时间")
    @TableField("start_time")
    private Date startTime;

    @Schema(description = "价格类型:0、尖;1、峰;2、平;3、谷;")
    @TableField("price_type")
    private Short priceType;

    @Schema(description = "电价")
    @TableField("elec_price")
    private BigDecimal elecPrice;

    @Schema(description = "服务费单价")
    @TableField("service_price")
    private BigDecimal servicePrice;

    @Schema(description = "备注")
    @TableField("remark")
    private String remark;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @Schema(description = "删除标志", accessMode = Schema.AccessMode.READ_ONLY)
    @TableField("del_flag")
    @TableLogic
    private Short delFlag;

    public int getHour(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }
}
