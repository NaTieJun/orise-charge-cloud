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
@TableName("sys_station")
public class SysStation implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    @TableField("station_id")
    private String stationId;

    @TableField("operator_id")
    private String operatorId;

    @TableField("equipment_owner_id")
    private String equipmentOwnerId;

    @TableField("station_name")
    private String stationName;

    @TableField("country_code")
    private String countryCode;

    @TableField("area_code")
    private String areaCode;

    @TableField("address")
    private String address;

    @TableField("station_tel")
    private String stationTel;

    @TableField("service_tel")
    private String serviceTel;

    @TableField("station_type")
    private Integer stationType;

    @TableField("station_status")
    private Short stationStatus;

    @TableField("park_nums")
    private Integer parkNums;

    @TableField("station_lng")
    private BigDecimal stationLng;

    @TableField("station_lat")
    private BigDecimal stationLat;

    @TableField("site_guide")
    private String siteGuide;

    @TableField("construction")
    private Integer construction;

    @TableField("pictures")
    private String pictures;

    @TableField("match_cars")
    private String matchCars;

    @TableField("park_info")
    private String parkInfo;

    @TableField("business_hours")
    private String businessHours;

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

    @TableField("electricity_fee")
    private String electricityFee;

    @TableField("service_fee")
    private String serviceFee;

    @TableField("park_fee")
    private String parkFee;

    @TableField("payment")
    private String payment;

    @TableField("support_order")
    private Short supportOrder;

    @TableField("remark")
    private String remark;

    @TableField("report_gov_flag")
    private Short reportGovFlag;

    @TableField("report_gov")
    private Short reportGov;

    @TableField("is_alone_apply")
    private Short isAloneApply;

    @TableField("account_number")
    private String accountNumber;

    @TableField("capacity")
    private BigDecimal capacity;

    @TableField("is_public_parking_lot")
    private Short isPublicParkingLot;

    @TableField("parking_lot_number")
    private String parkingLotNumber;

    @TableField("open_all_day")
    private Short openAllDay;

    @TableField("park_free")
    private Short parkFree;

    @TableField("park_fee_type")
    private Short parkFeeType;

    @TableField("unit_flag")
    private Integer unitFlag;

    @TableField("min_electricity_price")
    private BigDecimal minElectricityPrice;

    @TableField("subsidy_per_kwh")
    private BigDecimal subsidyPerKwh;

    @TableField("subsidy_year_max")
    private BigDecimal subsidyYearMax;

    @TableField("subsidy_operator")
    private String subsidyOperator;

    @TableField(exist = false)
    private Integer pileNum;

    @TableField(exist = false)
    private Integer gunNum;

    @TableField(exist = false)
    private String createUserName;

    @TableField(exist = false)
    private String updateUsername;

}
