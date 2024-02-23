package org.dromara.omind.baseplat.api.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.utils.DateUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.omind.api.common.utils.encrypt.AESUtils;
import org.dromara.omind.api.common.utils.encrypt.HMacMD5Util;
import org.dromara.omind.baseplat.api.constant.HlhtRet;
import org.dromara.omind.baseplat.api.domain.entity.SysOperator;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
@Schema(name = "HlhtDto",description = "互联互通标准请求对象")
public class HlhtDto {

    @Schema(name = "OperatorID", description = "运营商标识，即组织机构代码9位")
    @JsonProperty(value = "OperatorID")
    private String operatorID;

    @Schema(name = "Data", description = "数据体 AES128位加密")
    @JsonProperty(value = "Data")
    private String data;

    @Schema(name = "TimeStamp", description = "时间戳，接口请求时时间戳信息,格式为yyyyMMddHHmmss")
    @JsonProperty(value = "TimeStamp")
    private String timeStamp;

    @Schema(name = "Seq", description = "4位自增序列取自时间戳，同一秒内按序列自增长，新秒重计。如0001")
    @JsonProperty(value = "Seq")
    private String seq;

    @Schema(name = "Sig", description = "签名参数")
    @JsonProperty(value = "Sig")
    private String sig;

    /**
     * 请求体校验
     * @return
     */
    public HlhtResult checkValid(SysOperator sysOperator){
        if(TextUtils.isBlank(operatorID)){
            return HlhtResult.error(HlhtRet.ERROR_KEY, "缺少参数operatorID");
        }
        else if(TextUtils.isBlank(data)){
            return HlhtResult.error(HlhtRet.ERROR_KEY, "缺少参数data");
        }
        else if(TextUtils.isBlank(timeStamp)){
            return HlhtResult.error(HlhtRet.ERROR_KEY, "缺少参数timestamp");
        }
        else if(TextUtils.isBlank(seq)){
            return HlhtResult.error(HlhtRet.ERROR_KEY, "缺少参数seq");
        }
        else if(TextUtils.isBlank(sig)){
            return HlhtResult.error(HlhtRet.ERROR_KEY, "缺少参数sig");
        }

        String mySig = HMacMD5Util.getHMacMD5(sysOperator.getSigSecret(), operatorID, data, timeStamp, seq);
        mySig = HMacMD5Util.getHMacMD5(sysOperator.getSigSecret(), operatorID, data, timeStamp, seq);
        if (!sig.equals(mySig)) {
            return HlhtResult.error(HlhtRet.ERROR_SIG, "签名错误");
        }
        return HlhtResult.success();
    }

    public <T> T getDataObj(SysOperator sysOperator, Class<T> clazz){
        try {
            String jsonStr = AESUtils.decrypt(data, sysOperator.getDataSecret(), sysOperator.getDataSecretIv());
            T t = JsonUtils.parseObject(jsonStr, clazz);
            return t;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 请求参数编码
     * @param sysOperator
     * @param obj
     */
    public void setDataObj(SysOperator sysOperator, Object obj, String seqValue){
        try{
            String jsonStr = JsonUtils.toJsonString(obj);
            log.info(jsonStr);
            String encodeJsonStr = AESUtils.aes128CbcPKCS5Padding(sysOperator.getUserDataSecret(), sysOperator.getUserDataSecretIv(), jsonStr);
            this.data = encodeJsonStr;
            this.operatorID = sysOperator.getMyOperatorId();
            this.timeStamp = DateUtils.dateTimeNow();
            this.seq = seqValue;
            this.sig = HMacMD5Util.getHMacMD5(sysOperator.getUserSigSecret(), this.operatorID, this.data, this.timeStamp,this.seq);
            return;
        }
        catch (Exception e){
            log.error(e.toString());
        }
    }

    /**
     * 对于某些平台个别key转换
     * @param sysOperator
     * @param obj
     * @param seqValue
     * @param convertType 0不转换  1 转换
     */
    public void setDataObj(SysOperator sysOperator, Object obj, String seqValue, int convertType){
        try{
            String jsonStr = JsonUtils.toJsonString(obj);
            if(convertType == 2) {
                jsonStr = jsonStr.replace("ServiceMoney", "SeviceMoney");
            }
            String encodeJsonStr = AESUtils.aes128CbcPKCS5Padding(sysOperator.getUserDataSecret(), sysOperator.getUserDataSecretIv(), jsonStr);
            this.data = encodeJsonStr;
            this.operatorID = sysOperator.getMyOperatorId();
            this.timeStamp = DateUtils.dateTimeNow();
            this.seq = seqValue;
            this.sig = HMacMD5Util.getHMacMD5(sysOperator.getUserSigSecret(), this.operatorID, this.data, this.timeStamp,this.seq);
            return;
        }
        catch (Exception e){
            log.error(e.toString());
        }
    }

}
