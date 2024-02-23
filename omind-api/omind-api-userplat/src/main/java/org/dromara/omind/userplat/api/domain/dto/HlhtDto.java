package org.dromara.omind.userplat.api.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.TextUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.omind.api.common.utils.encrypt.AESUtils;
import org.dromara.omind.api.common.utils.encrypt.HMacMD5Util;
import org.dromara.omind.userplat.api.constant.HlhtRet;
import org.dromara.omind.userplat.api.domain.entity.OmindOperatorEntity;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(name = "HlhtDto",description = "互联互通标准请求对象")
@Slf4j
public class HlhtDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(name = "OperatorID", description = "运营商标识")
    @JsonProperty(value = "OperatorID")
    private String operatorID;

    @Schema(name = "Data", description = "数据体")
    @JsonProperty(value = "Data")
    private String data;

    @Schema(name = "TimeStamp", description = "时间戳")
    @JsonProperty(value = "TimeStamp")
    private String timeStamp;

    @Schema(name = "Seq", description = "4位自增序列取自时间戳，同一秒内按序列自增长，新秒重计")
    @JsonProperty(value = "Seq")
    private String seq;

    @Schema(name = "Sig", description = "签名参数")
    @JsonProperty(value = "Sig")
    private String sig;

    /**
     * 请求体校验
     *
     * @return
     */
    public HlhtResult checkValid(OmindOperatorEntity omindOperator) {
        //首先校验参数是否完整
        if (TextUtils.isBlank(operatorID)) {
            log.error("[互联互通]--运营商id为空");
            return HlhtResult.error(HlhtRet.ERROR_KEY, "缺少参数operatorID");
        } else if (TextUtils.isBlank(data)) {
            log.error("[互联互通]--数据data为空");
            return HlhtResult.error(HlhtRet.ERROR_KEY, "缺少参数data");
        } else if (TextUtils.isBlank(timeStamp)) {
            log.error("[互联互通]--时间戳timestamp为空");
            return HlhtResult.error(HlhtRet.ERROR_KEY, "缺少参数timestamp");
        } else if (TextUtils.isBlank(seq)) {
            log.error("[互联互通]--序列号seq为空");
            return HlhtResult.error(HlhtRet.ERROR_KEY, "缺少参数seq");
        } else if (TextUtils.isBlank(sig)) {
            log.error("[互联互通]--签名sig为空");
            return HlhtResult.error(HlhtRet.ERROR_KEY, "缺少参数sig");
        }

        String mySig = HMacMD5Util.getHMacMD5(omindOperator.getSigSecret(), operatorID, data, timeStamp, seq);

        if (!sig.equals(mySig)) {
            log.error("[互联互通]--签名sig不一致:reqSig=" + sig + "|genSig=" + mySig);
            return HlhtResult.error(HlhtRet.ERROR_SIG, "签名错误");
        }
        return HlhtResult.success();
    }

    public <T> T getDataObj(OmindOperatorEntity sysOperator, Class<T> clazz) {
        String jsonStr = "";
        try {
            jsonStr = AESUtils.decrypt(data, sysOperator.getDataSecret(), sysOperator.getDataSecretIv());
            T t = JsonUtils.parseObject(jsonStr, clazz);
            return t;
        } catch (Exception e) {
            log.error(data);
            log.error(JsonUtils.toJsonString(sysOperator));
            log.error(jsonStr);
            log.error(e.toString());
            return null;
        }
    }

}
