package org.dromara.omind.baseplat.controller.hlht;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.http.util.TextUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.omind.api.common.utils.encrypt.AESUtils;
import org.dromara.omind.api.common.utils.encrypt.HMacMD5Util;
import org.dromara.omind.api.common.utils.uuid.IdUtils;
import org.dromara.omind.baseplat.api.constant.HlhtRet;
import org.dromara.omind.baseplat.api.domain.dto.HlhtDto;
import org.dromara.omind.baseplat.api.domain.dto.HlhtResult;
import org.dromara.omind.baseplat.api.domain.entity.SysOperator;
import org.dromara.omind.baseplat.api.domain.request.QueryTokenData;
import org.dromara.omind.baseplat.api.domain.response.QueryTokenResponseData;
import org.dromara.omind.baseplat.api.constant.HlhtRedisKey;
import org.dromara.omind.baseplat.service.SysOperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@Tag(name = "EvcsAuthV1Controller", description = "[V1]互联互通——平台鉴权接口V1")
@RestController
@RequestMapping("/evcs/v1/")
public class EvcsAuthV1Controller {

    @Autowired
    SysOperatorService operatorService;

    @Operation(summary = "[OK]获取token（建议每天更新一次，最长有效期不超过7天）")
    @PostMapping("query_token")
    public HlhtResult queryToken(@RequestBody HlhtDto hlhtDto){

        String operatorId = hlhtDto.getOperatorID();
        SysOperator sysOperator = operatorService.getOperatorById(operatorId);
        if(sysOperator == null){
            return HlhtResult.error(HlhtRet.ERROR_PARAM, "参数错误-运营商ID");
        }
        HlhtResult ret = hlhtDto.checkValid(sysOperator);
        if(ret.getRet() != HlhtRet.OK){
            return ret;
        }

        QueryTokenData queryTokenData  = hlhtDto.getDataObj(sysOperator, QueryTokenData.class);
        if(TextUtils.isBlank(queryTokenData.getOperatorSecret()) || TextUtils.isBlank(queryTokenData.getOperatorID())){
            return HlhtResult.error(HlhtRet.ERROR_PARAM, "参数错误");
        }
        else if(!queryTokenData.getOperatorID().equals(sysOperator.getOperatorId()) || !queryTokenData.getOperatorSecret().equals(sysOperator.getOperatorSecret())){
            return HlhtResult.error(HlhtRet.ERROR_PARAM, "参数错误");
        }

        //这里生成登录token，需要放到redis里
        String token = IdUtils.fastUUID();
        RedisUtils.setCacheObject(HlhtRedisKey.OPERATOR_TOKEN + token, operatorId, Duration.ofDays(7));

        QueryTokenResponseData responseData = new QueryTokenResponseData();
        responseData.setOperatorID(sysOperator.getOperatorId());
        responseData.setSuccStat((short)0);
        responseData.setAccessToken(token);
        responseData.setTokenAvailableTime(7*24*3600L);
        responseData.setFailReason((short)0);
        String responseJson = JsonUtils.toJsonString(responseData);
        HlhtResult result = new HlhtResult();
        result.setData(AESUtils.aes128CbcPKCS5Padding(sysOperator.getDataSecret(), sysOperator.getDataSecretIv(), responseJson));
        result.setRet(HlhtRet.OK);
        result.setMsg("");
        result.setSig(HMacMD5Util.getHMacMD5(sysOperator.getSigSecret(), result.getRet() + result.getMsg() + result.getData()));
        return result;
    }

}
