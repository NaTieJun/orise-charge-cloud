package org.dromara.omind.baseplat.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.omind.baseplat.domain.vo.PriceInfoData;

import java.io.Serializable;
import java.util.List;

@Schema(description = "编辑价格模版")
@Data
public class PriceEditRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "价格模版ID", requiredMode = Schema.RequiredMode.REQUIRED)
    Long priceCode;

//    @Schema(description = "价格列表", requiredMode = Schema.RequiredMode.AUTO)
//    List<PriceInfoData> priceList;

    @Schema(description = "备注（搜索用）", requiredMode = Schema.RequiredMode.AUTO)
    String remark;

}
