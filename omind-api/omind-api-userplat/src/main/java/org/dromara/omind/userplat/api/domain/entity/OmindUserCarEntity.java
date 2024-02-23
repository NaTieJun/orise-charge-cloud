package org.dromara.omind.userplat.api.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Schema(description = "小程序用户车辆")
@Data
@TableName("omind_user_car")
public class OmindUserCarEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "id")
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "车牌号")
    @TableField("plate_no")
    private String plateNo;

    @Schema(description = "车辆VIN")
    @TableField("car_vin")
    private String carVin;

    @Schema(description = "发动机号")
    @TableField("engine_no")
    private String engineNo;

    @Schema(description = "车辆类型")
    @TableField("vehicle_type")
    private String vehicleType;

    @Schema(description = "品牌型号")
    @TableField("car_model")
    private String carModel;

    @Schema(description = "车辆所有人")
    @TableField("owner")
    private String owner;

    @Schema(description = "住址")
    @TableField("address")
    private String address;

    @Schema(description = "使用性质:运营、非运营")
    @TableField("use_character")
    private String useCharacter;

    @Schema(description = "注册日期")
    @TableField("register_date")
    private Date registerDate;

    @Schema(description = "发证日期")
    @TableField("issue_date")
    private Date issueDate;

    @Schema(description = "行驶证图片json串")
    @TableField("license_imgs")
    private String licenseImgs;

    @Schema(description = "审核状态:0、待审核;1、审核通过;2、审核不通过;3、不需审核")
    @TableField("check_state")
    private Short checkState;

    @Schema(description = "认证状态:0、不认证;1、待认证;2、认证通过;3、认证不通过")
    @TableField("auth_state")
    private Short authState;

    @Schema(description = "备注")
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

    @Schema(description = "用户名称")
    @TableField(exist = false)
    private String nickName;

    @Schema(description = "手机号")
    @TableField(exist = false)
    private String mobile;

}
