package org.dromara.omind.baseplat.api.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("sys_price")
public class SysPrice implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    @TableField("price_code")
    private Long priceCode;

    @TableField("start_time")
    private Date startTime;

    @TableField("price_type")
    private Short priceType;

    @TableField("elec_price")
    private BigDecimal elecPrice;

    @TableField("service_price")
    private BigDecimal servicePrice;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField("remark")
    private String remark;

    @Schema(description = "价格主信息标记")
    @JsonProperty(value = "mainPoint", access = JsonProperty.Access.WRITE_ONLY)
    @TableField("main_point")
    private Short mainPoint;

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
