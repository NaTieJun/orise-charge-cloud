package org.dromara.omind.userplat.api.domain.dto;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.TextUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.omind.api.common.utils.encrypt.AESUtils;
import org.dromara.omind.api.common.utils.encrypt.HMacMD5Util;
import org.dromara.omind.userplat.api.constant.HlhtRet;
import org.dromara.omind.userplat.api.domain.entity.OmindOperatorEntity;

import java.io.Serializable;

@Schema(name = "HlhtResult", description = "互联互通返回数据体")
@Data
@Slf4j
public class HlhtResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 返回状态码
     */
    public static final String RET_TAG = "Ret";

    @Schema(name = "Ret", description = "状态码")
    @JsonProperty(value = "Ret")
    private int ret;

    /**
     * 返回内容
     */
    public static final String MSG_TAG = "Msg";

    @Schema(name = "Msg", description = "返回内容")
    @JsonProperty(value = "Msg")
    private String msg;

    /**
     * 返回数据对象
     */
    public static final String DATA_TAG = "Data";

    @Schema(name = "Data",description = "数据对象")
    @JsonProperty(value = "Data")
    private String data;


    public static final String SIG_TAG = "Sig";

    @Schema(name = "Sig",description = "数字签名")
    @JsonProperty(value = "Sig")
    private String sig;

    /**
     * 初始化一个HlhtResult 对象，使其表示一个空消息。
     */
    public HlhtResult() {
    }

    /**
     * 初始化HlhtResult 对象
     *
     * @param ret 状态码
     * @param msg 返回内容
     */
    public HlhtResult(int ret, String msg) {
        this();
        this.ret = ret;
        this.msg = msg;
    }

    /**
     * 初始化 HlhtResult 对象
     *
     * @param ret  状态码
     * @param msg  返回内容
     * @param data 数据对象
     */
    public HlhtResult(int ret, String msg, String data) {
        this();
        this.ret = ret;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 返回成功消息
     *
     * @return 成功消息
     */
    public static HlhtResult success() {
        return HlhtResult.success("操作成功");
    }

    /**
     * 返回成功数据
     *
     * @return 成功消息
     */
    public static <T> HlhtResult success(String data) {
        return HlhtResult.success("操作成功", data);
    }


    /**
     * 返回成功消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 成功消息
     */
    public static HlhtResult success(String msg, String data) {
        return (HlhtResult) new HlhtResult(HlhtRet.OK, msg, data);
    }

    /**
     * 返回错误消息
     *
     * @return
     */
    public static HlhtResult error() {
        return HlhtResult.error("操作失败");
    }

    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @return 警告消息
     */
    public static HlhtResult error(String msg) {
        return HlhtResult.error(msg, null);
    }

    /**
     * 返回错误消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 警告消息
     */
    public static HlhtResult error(String msg, String data) {
        return new HlhtResult(HlhtRet.ERROR_SYS, msg, data);
    }

    /**
     * 返回错误消息
     *
     * @param code 状态码
     * @param msg  返回内容
     * @return 警告消息
     */
    public static HlhtResult error(int code, String msg) {
        return new HlhtResult(code, msg, null);
    }

    public JSONObject build() {
        JSONObject json = new JSONObject();
        json.put("Ret", getRet());
        json.put("Msg", getMsg());
        json.put("Data", getData());
        json.put("Sig", getSig());
        return json;
    }

    /**
     * 响应体校验
     *
     * @return
     */
    public Boolean checkValid(OmindOperatorEntity odOperatorInfo) {
        boolean checkFlag = false;
        //首先校验参数是否完整
        if (TextUtils.isBlank(data)) {
            log.error("[互联互通]--数据data为空");
        } else if (msg == null) {
            log.error("[互联互通]--msg字段不存在");
        } else if (TextUtils.isBlank(sig)) {
            log.error("[互联互通]--签名sig为空");
        }

        String newData = ret + msg + data;
        log.info("ret=" + ret + "|msg=" + msg + "|data=" + data + "|sig=" + sig);
        String mySig = HMacMD5Util.getHMacMD5(odOperatorInfo.getBaseSigSecret(), "", newData, "", "");

        if (!sig.equals(mySig)) {
            log.error("[互联互通]--签名sig不一致:reqSig=" + sig + "|genSig=" + mySig);
        }
        checkFlag = true;
        return checkFlag;
    }

    public <T> T getDataObj(OmindOperatorEntity odOperatorInfo, Class<T> clazz) {
        try {
            String jsonStr = AESUtils.decrypt(this.data, odOperatorInfo.getBaseDataSecret(), odOperatorInfo.getBaseDataSecretIv());
            T t = JsonUtils.parseObject(jsonStr, clazz);
            return t;
        } catch (Exception e) {
            return null;
        }
    }
}