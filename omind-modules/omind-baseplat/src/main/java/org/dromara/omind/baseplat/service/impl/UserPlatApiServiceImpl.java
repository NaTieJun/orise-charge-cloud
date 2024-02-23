package org.dromara.omind.baseplat.service.impl;

import lombok.extern.log4j.Log4j2;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.omind.baseplat.api.constant.HlhtRedisKey;
import org.dromara.omind.baseplat.api.constant.HlhtRet;
import org.dromara.omind.baseplat.api.domain.dto.HlhtDto;
import org.dromara.omind.baseplat.api.domain.dto.HlhtResult;
import org.dromara.omind.baseplat.api.domain.entity.SysOperator;
import org.dromara.omind.baseplat.api.domain.request.QueryEquipBusinessPolicyData;
import org.dromara.omind.baseplat.api.domain.request.QueryTokenData;
import org.dromara.omind.baseplat.api.domain.response.QueryEquipBusinessPolicyResponseData;
import org.dromara.omind.baseplat.api.domain.response.QueryTokenResponseData;
import org.dromara.omind.baseplat.api.service.RemoteSeqService;
import org.dromara.omind.baseplat.client.UPlatAuthV1Client;
import org.dromara.omind.baseplat.client.UPlatBusinessV1Client;
import org.dromara.omind.baseplat.service.SysConnectorService;
import org.dromara.omind.baseplat.service.SysOperatorService;
import org.dromara.omind.baseplat.service.SysPriceService;
import org.dromara.omind.baseplat.service.UserPlatApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;

@Log4j2
@Service
public class UserPlatApiServiceImpl implements UserPlatApiService {


    @Resource
    UPlatAuthV1Client authV1Client;

    @Resource
    UPlatBusinessV1Client businessV1Client;

    @Autowired
    @Lazy
    RemoteSeqService remoteSeqService;

    @Autowired
    @Lazy
    SysOperatorService operatorService;

    @Autowired
    SysConnectorService connectorService;

    @Autowired
    SysPriceService priceService;

    @Autowired
    @Lazy
    UserPlatApiService selfService;

    @Override
    public QueryTokenResponseData authLogin(SysOperator sysOperator) {

        String seq = remoteSeqService.getHlhtRequestSeq();
        HlhtDto hlhtDto = new HlhtDto();
        QueryTokenData queryTokenData = new QueryTokenData();
        queryTokenData.setOperatorID(sysOperator.getMyOperatorId());
        queryTokenData.setOperatorSecret(sysOperator.getUserOperatorSecret());
        hlhtDto.setDataObj(sysOperator, queryTokenData, seq);
        HlhtResult hlhtResult = authV1Client.queryToken(sysOperator.getHost(), hlhtDto);
        QueryTokenResponseData queryTokenResponseData = null;
        try {
            queryTokenResponseData = hlhtResult.getDataObj(sysOperator, QueryTokenResponseData.class);
            if (queryTokenResponseData != null && !TextUtils.isBlank(queryTokenResponseData.getAccessToken())) {
                String key = HlhtRedisKey.UPLAT_TOKEN + sysOperator.getOperatorId();
                //10分钟到期，防止平台意外过期
                if(queryTokenResponseData.getTokenAvailableTime() > 600) {
                    RedisUtils.setCacheObject(key, queryTokenResponseData.getAccessToken(), Duration.ofMinutes(10));
                }
                else{
                    RedisUtils.setCacheObject(key, queryTokenResponseData.getAccessToken(), Duration.ofSeconds(queryTokenResponseData.getTokenAvailableTime() - 10));
                }
            }
        }catch (Exception ex){
            log.error(ex.toString(), ex);
        }
        return queryTokenResponseData;

    }

    @Override
    public QueryEquipBusinessPolicyResponseData policyInfo(String connectorId) {

        SysOperator sysOperator = connectorService.getDefaultOperatorByConnectorId(connectorId);

        String seq = remoteSeqService.getHlhtRequestSeq();
        HlhtDto hlhtDto = new HlhtDto();
        QueryEquipBusinessPolicyData queryEquipBusinessPolicyData = new QueryEquipBusinessPolicyData();
        queryEquipBusinessPolicyData.setConnectorID(connectorId);
        queryEquipBusinessPolicyData.setEquipBizSeq(seq);
        hlhtDto.setDataObj(sysOperator, queryEquipBusinessPolicyData, seq);
        String token = selfService.getTokenAuto(sysOperator);
        HlhtResult hlhtResult = businessV1Client.queryEquipBusinessPolicy(sysOperator.getHost(), sysOperator.getOperatorId(),token, hlhtDto);
        QueryEquipBusinessPolicyResponseData queryEquipBusinessPolicyResponseData = null;
        try{
            queryEquipBusinessPolicyResponseData = hlhtResult.getDataObj(sysOperator, QueryEquipBusinessPolicyResponseData.class);

            //更新到充电桩数据
            if(queryEquipBusinessPolicyResponseData.getSuccStat() == HlhtRet.OK){
                if(queryEquipBusinessPolicyResponseData.getSumPeriod() > 0){
                    try {
                        //SysConnector sysConnector = connectorService.getConnectorById(queryEquipBusinessPolicyResponseData.getConnectorID());
                        //priceService.updatePriceList(sysConnector, queryEquipBusinessPolicyResponseData.getPolicyInfos());
                        log.info("仅记录，不使用用户平台充电价格：" + JsonUtils.toJsonString(queryEquipBusinessPolicyData));
                    }
                    catch (BaseException ex){
                        log.error("[充电桩插枪]更新价格失败 connectorId=" + connectorId, ex);
                    }
                }
            }

        }
        catch (Exception ex){
            log.error("[价格策略]解析失败，connector=" + connectorId, ex);
        }
        return queryEquipBusinessPolicyResponseData;
    }

    @Override
    public String getTokenAuto(SysOperator sysOperator) {
        String key = HlhtRedisKey.UPLAT_TOKEN + sysOperator.getOperatorId();
        if(RedisUtils.hasKey(key) && RedisUtils.getCacheObject(key) != null){
            String value = RedisUtils.getCacheObject(key);
            if(!TextUtils.isBlank(value)){
                return "Bearer " + value;
            }
        }
        try{
            QueryTokenResponseData responseData = selfService.authLogin(sysOperator);
            if(responseData != null && !TextUtils.isBlank(responseData.getAccessToken())){
                return "Bearer " + responseData.getAccessToken();
            }
        }
        catch (Exception ex){
            log.error("token获取失败 "+ ex.toString(), ex);
        }
        return null;
    }

    @Override
    public String refreshToken(SysOperator sysOperator) {
        String key = HlhtRedisKey.UPLAT_TOKEN + sysOperator.getOperatorId();
        if(RedisUtils.hasKey(key)){
            RedisUtils.deleteObject(key);
        }
        try{
            QueryTokenResponseData responseData = selfService.authLogin(sysOperator);
            if(responseData != null && !TextUtils.isBlank(responseData.getAccessToken())){
                return "Bearer " + responseData.getAccessToken();
            }
        }
        catch (Exception ex){
            log.error("token获取失败 "+ ex.toString(), ex);
        }
        return null;
    }

}
