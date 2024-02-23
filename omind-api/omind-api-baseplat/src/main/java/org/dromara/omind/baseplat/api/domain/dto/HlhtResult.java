package org.dromara.omind.baseplat.api.domain.dto;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.dromara.omind.api.common.utils.encrypt.AESUtils;
import org.dromara.omind.baseplat.api.constant.HlhtRet;
import org.dromara.omind.baseplat.api.domain.entity.SysOperator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Optional;

@Slf4j
@Schema(name = "HlhtResult", description = "互联互通返回数据体")
@Data
public class HlhtResult implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonIgnore
    private HashMap<String, Object> map;

    /**
     * 状态码
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
     * 数据对象
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
     * 初始化一个新创建的 HlhtResult 对象，使其表示一个空消息。
     */
    public HlhtResult() {
        this.map = new HashMap<>();
    }

    /**
     * 初始化一个新创建的 HlhtResult 对象
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
     * 初始化一个新创建的 HlhtResult 对象
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

    public void put(String key, Object value) {
        this.map.put(key, value);
    }

    public JSONObject build() {
        JSONObject json = new JSONObject();
        json.put("Ret", getRet());
        json.put("Msg", getMsg());
        json.put("Data", getData());
        json.put("Sig", getSig());
        Optional.ofNullable(getMap()).ifPresent(m -> m.forEach(json::put));
        return json;
    }

    public <T> T getDataObj(SysOperator sysOperator, Class<T> clazz){
        try {

            String jsonStr = AESUtils.decrypt(this.data, sysOperator.getUserDataSecret(), sysOperator.getUserDataSecretIv());
            log.info(jsonStr);
            T t = JSONObject.parseObject(jsonStr, clazz);
            return t;
        } catch (Exception e) {
            log.error(e.toString(), e);
            return null;
        }
    }
}
