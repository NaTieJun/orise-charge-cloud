package org.dromara.omind.userplat.service.hlht.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.core.utils.DateUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.omind.api.common.utils.SeqUtil;
import org.dromara.omind.api.common.utils.encrypt.AESUtils;
import org.dromara.omind.api.common.utils.encrypt.HMacMD5Util;
import org.dromara.omind.api.common.utils.uuid.IdUtils;
import org.dromara.omind.userplat.api.constant.HlhtRedisKey;
import org.dromara.omind.userplat.api.constant.HlhtRet;
import org.dromara.omind.userplat.api.domain.dto.HlhtDto;
import org.dromara.omind.userplat.api.domain.dto.HlhtResult;
import org.dromara.omind.userplat.api.domain.dto.HlhtTokenDto;
import org.dromara.omind.userplat.api.domain.entity.OmindOperatorEntity;
import org.dromara.omind.userplat.api.domain.request.QueryTokenData;
import org.dromara.omind.userplat.api.domain.response.QueryTokenResponseData;
import org.dromara.omind.userplat.client.BPlatAuthV1Client;
import org.dromara.omind.userplat.service.OmindOperatorService;
import org.dromara.omind.userplat.service.hlht.HlhtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;

@Slf4j
@Service
public class HlhtTokenServiceImpl implements HlhtTokenService {

    @Autowired
    @Lazy
    OmindOperatorService odOperatorService;

    @Autowired
    @Lazy
    HlhtTokenService selfService;

    @Resource
    BPlatAuthV1Client bPlatAuthV1Client;

    @Override
    public HlhtResult getToken(HlhtDto hlhtDto) {
        //调用方operatorId
        log.info("query-token-----requestData===" + hlhtDto);
        String operatorId = hlhtDto.getOperatorID();
        OmindOperatorEntity sysOperator = odOperatorService.selectOperatorInfoById(operatorId);
        if (sysOperator == null) {
            return HlhtResult.error(HlhtRet.ERROR_PARAM, "参数错误-运营商ID");
        }
        HlhtResult ret = hlhtDto.checkValid(sysOperator);
        if (ret.getRet() != HlhtRet.OK) {
            return ret;
        }

        QueryTokenData queryTokenData = hlhtDto.getDataObj(sysOperator, QueryTokenData.class);
        if (TextUtils.isBlank(queryTokenData.getOperatorSecret()) || TextUtils.isBlank(queryTokenData.getOperatorID())) {
            return HlhtResult.error(HlhtRet.ERROR_PARAM, "参数无效");
        } else if (!queryTokenData.getOperatorID().equals(sysOperator.getOperatorId()) || !queryTokenData.getOperatorSecret().equals(sysOperator.getOperatorSecret())) {
            return HlhtResult.error(HlhtRet.ERROR_PARAM, "参数错误");
        }

        //这里生成登录token，需要放到redis里
        String token = IdUtils.fastUUID();
        long tokenAvailableTime = 7 * 24 * 3600L;
        //支持多个token同时存在
        String baseTokenKey = HlhtRedisKey.BASE_OPERATOR_TOKEN + token;
        RedisUtils.setCacheObject(baseTokenKey, operatorId, Duration.ofSeconds(tokenAvailableTime));

        QueryTokenResponseData responseData = new QueryTokenResponseData();
        responseData.setOperatorID(sysOperator.getOperatorId());
        responseData.setSuccStat((short) 0);
        responseData.setAccessToken(token);
        responseData.setTokenAvailableTime(tokenAvailableTime);
        responseData.setFailReason((short) 0);
        String responseJson = JsonUtils.toJsonString(responseData);
        HlhtResult result = new HlhtResult();
        String data = AESUtils.aes128CbcPKCS5Padding(sysOperator.getDataSecret(), sysOperator.getDataSecretIv(), responseJson);
        result.setData(data);
        result.setRet(HlhtRet.OK);
        result.setMsg("");

        String newData = result.getRet() + result.getMsg() + result.getData();
        result.setSig(HMacMD5Util.getHMacMD5(sysOperator.getSigSecret(), "", newData, "", ""));

        return result;
    }

    @Override
    public HlhtTokenDto queryToken(String userOperatorId, Short platType) throws BaseException {
        OmindOperatorEntity odOperatorInfoEntity = odOperatorService.selectOperatorInfo(userOperatorId, platType);
        if (odOperatorInfoEntity == null) {
            throw new BaseException("运营商不存在");
        }
        String url = odOperatorInfoEntity.getApiUrl();
        String data = selfService.getTokenRequestData(odOperatorInfoEntity);
        String operatorId = userOperatorId;
        String seq = SeqUtil.getUniqueInstance().getSeq();
        String timeStamp = DateUtils.dateTimeNow();
        String sig = selfService.getRequestSig(odOperatorInfoEntity, timeStamp, seq);

        String accessToken = "";
        Integer accessTokenTime = 0;

        HlhtDto hlhtDtoData = new HlhtDto();
        hlhtDtoData.setData(data);
        hlhtDtoData.setSeq(seq);
        hlhtDtoData.setTimeStamp(timeStamp);
        hlhtDtoData.setOperatorID(operatorId);
        hlhtDtoData.setSig(sig);

        HlhtResult tokenResult = bPlatAuthV1Client.queryToken(url,hlhtDtoData);
        //记录请求日志
        log.info("获取token--query_token--undecrypt==" + tokenResult);

        if (tokenResult.getRet() == 0) {
            try {
                String tokenData = AESUtils.decrypt(tokenResult.getData(), odOperatorInfoEntity.getBaseDataSecret(), odOperatorInfoEntity.getBaseDataSecretIv());
                //记录请求日志
                log.info("获取token--query_token--decrypt==" + tokenData);
                JSONObject tokenDataObj = JSONObject.parseObject(tokenData);
                Short succStat = tokenDataObj.getShort("SuccStat");

                if (succStat == 0) {
                    accessToken = tokenDataObj.getString("AccessToken");
                    accessTokenTime = tokenDataObj.getInteger("TokenAvailableTime");
                    //token缓存至redis
                    String userTokenKey = HlhtRedisKey.USER_OPERATOR_TOKEN + operatorId + ":" + platType;
                    RedisUtils.setCacheObject(userTokenKey, accessToken, Duration.ofSeconds(accessTokenTime));
                }
            } catch (BaseException be) {
                log.error("token-queryToken-error", be);
                throw be;
            } catch (Exception e) {
                log.error("token-queryToken-error", e);
                throw new BaseException("AES解密失败");
            }
        }

        HlhtTokenDto hlhtTokenDto = new HlhtTokenDto();
        hlhtTokenDto.setAccessToken(accessToken);
        hlhtTokenDto.setTokenAvailableTime(accessTokenTime);

        return hlhtTokenDto;
    }

    @Override
    public String checkToken(String userOperatorId, Short platType) throws BaseException {
        String token = "";
        String userTokenKey = HlhtRedisKey.USER_OPERATOR_TOKEN + userOperatorId + ":" + platType;
        if (RedisUtils.hasKey(userTokenKey)) {
            token = RedisUtils.getCacheObject(userTokenKey);
            log.info("token----------redis");
        } else {
            HlhtTokenDto hlhtTokenDto = selfService.queryToken(userOperatorId, platType);
            token = hlhtTokenDto.getAccessToken();
            log.info("token-----------api");
        }

        return token;
    }

    @Override
    public String getTokenRequestData(OmindOperatorEntity odOperatorInfoEntity) throws BaseException {
        String data = "";
        QueryTokenData queryTokenData = new QueryTokenData();
        queryTokenData.setOperatorID(odOperatorInfoEntity.getUserOperatorId());
        queryTokenData.setOperatorSecret(odOperatorInfoEntity.getBaseOperatorSecret());
        data = JsonUtils.toJsonString(queryTokenData);

        return AESUtils.aes128CbcPKCS5Padding(odOperatorInfoEntity.getBaseDataSecret(), odOperatorInfoEntity.getBaseDataSecretIv(), data);
    }

    @Override
    public String getRequestSig(OmindOperatorEntity odOperatorInfoEntity, String timeStamp, String seq) {
        String sig = "";
        String data = selfService.getTokenRequestData(odOperatorInfoEntity);
        String operatorID = odOperatorInfoEntity.getUserOperatorId();

        sig = HMacMD5Util.getHMacMD5(odOperatorInfoEntity.getBaseSigSecret(), operatorID, data, timeStamp, seq);
        return sig;
    }
}
