package org.dromara.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("sys_area")
public class SysArea implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 区域ID
     */
    @Schema(description = "区域ID")
    @TableId(type = IdType.INPUT)
    @TableField("id")
    private Integer id;


    /**
     * 父ID
     */
    @Schema(description = "父ID")
    @TableField("parent_id")
    private Integer parentId;


    /**
     * 层级
     */
    @Schema(description = "层级")
    @TableField("level")
    private Integer level;


    /**
     * 区域名称
     */
    @Schema(description = "区域名称")
    @TableField("name")
    private String name;


    /**
     * 拼音首字母
     */
    @Schema(description = "拼音首字母")
    @TableField("e_prefix")
    private String ePrefix;


    /**
     * 拼音名称
     */
    @Schema(description = "拼音名称")
    @TableField("e_name")
    private String eName;


    /**
     * 对外区域ID
     */
    @Schema(description = "对外区域ID")
    @TableField("ext_id")
    private Long extId;


    /**
     * 区域对外名称
     */
    @Schema(description = "区域对外名称")
    @TableField("ext_name")
    private String extName;


    /**
     * 创建人id
     */
    @Schema(description = "创建人id")
    @TableField("create_by_id")
    private Long createById;


    /**
     * 更新人id
     */
    @Schema(description = "更新人id")
    @TableField("update_by_id")
    private Long updateById;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(hidden = true)
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 删除标识 0 正常 1 删除
     */
    @Schema(description = "删除标识 0 正常 1 删除")
    @TableField("del_flag")
    private Integer delFlag;


}
