package org.dromara.system.api.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "区域vo")
public class RemoteAreaVo implements Serializable {

    private static final long serialVersionUID = 1L; //1

    /**
     * 区域ID
     */
    @Schema(description = "区域ID")
    private Integer id;


    /**
     * 父ID
     */
    @Schema(description = "父ID")
    private Integer parentId;


    /**
     * 层级
     */
    @Schema(description = "层级")
    private Integer level;


    /**
     * 区域名称
     */
    @Schema(description = "区域名称")
    private String name;


    /**
     * 拼音首字母
     */
    @Schema(description = "拼音首字母")
    private String ePrefix;


    /**
     * 拼音名称
     */
    @Schema(description = "拼音名称")
    private String eName;


    /**
     * 对外区域ID
     */
    @Schema(description = "对外区域ID")
    private Long extId;


    /**
     * 区域对外名称
     */
    @Schema(description = "区域对外名称")
    private String extName;

}
