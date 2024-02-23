package org.dromara.omind.userplat.api.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Schema(description = "用户意见反馈表")
@Data
@TableName("omind_feedback")
public class OmindFeedbackEntity  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "id")
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "充电设备接口编码")
    @TableField("connector_id")
    private String connectorId;

    @Schema(description = "意见反馈类型 1、充电站;2、充电桩;3、充电枪;50、其他")
    @TableField("feedback_type")
    private Short feedbackType;

    @Schema(description = "意见反馈内容")
    @TableField("feedback_content")
    private String feedbackContent;

    @Schema(description = "图片列表JSON")
    @TableField("imgs")
    private String imgs;

    @Schema(description = "是否回复:0、未回复;1、已回复")
    @TableField("reply_content")
    private String replyContent;

    @Schema(description = "是否回复:0、未回复;1、已回复")
    @TableField(value = "reply_flag")
    private Short replyFlag;

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

    @Schema(description = "图片URL列表")
    @TableField(exist = false)
    private List<String> imgUrls;

    @Schema(description = "用户名称")
    @TableField(exist = false)
    private String userName;

    @Schema(description = "手机号")
    @TableField(exist = false)
    private String mobile;

    @Schema(description = "用户头像")
    @TableField(exist = false)
    private String avatar;

    @Schema(description = "反馈类型:1、充电站;2、充电桩;3、充电枪;50、其他")
    @TableField(exist = false)
    private String feedbackTypeName;
}
