package org.dromara.omind.baseplat.api.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("sys_charge_order_item")
public class SysChargeOrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    @TableField("start_charge_seq")
    private String startChargeSeq;

    @TableField("start_time")
    private Date startTime;

    @TableField("end_time")
    private Date endTime;

    @TableField("elec_price")
    private BigDecimal elecPrice;

    @TableField("service_price")
    private BigDecimal sevicePrice;

    @TableField("power")
    private BigDecimal power;

    @TableField("elec_money")
    private BigDecimal elecMoney;

    @TableField("service_money")
    private BigDecimal serviceMoney;

}
