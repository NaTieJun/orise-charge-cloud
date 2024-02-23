package org.dromara.omind.mp.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.omind.api.common.utils.encrypt.Md5Utils;
import org.dromara.omind.mp.constant.XcxRedisKey;
import org.dromara.omind.mp.domain.request.SignRequest;
import org.dromara.omind.mp.service.OmindAppService;
import org.dromara.omind.userplat.api.domain.entity.OmindAppEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SignUtil {
    private final static long MAX_SIGN_TIMEOUT = 24 * 3600;


    @Autowired
    @Lazy
    OmindAppService appService;

    @Autowired
    @Lazy
    SignUtil selfUtil;


    /**
     * 校验token和sign
     * @param token         token值
     * @param signRequest   sign部分
     * @return 0 sign正确 token无效； -1 sign无效 -2时间戳异常  >0 返回UID sign token均有效
     */
    public long checkTokenAndSign(String token, SignRequest signRequest) throws BaseException
    {
        if(signRequest == null || TextUtils.isBlank(signRequest.getSign()) || TextUtils.isBlank(signRequest.getAppkey())
                || TextUtils.isBlank(signRequest.getNoncecode()) || signRequest.getTm() == null){
            throw new BaseException("签名(DATA)验证失败");
        }
        //验证sign 防止数据爬取 由于采用https，不再过度约束内容一致性，因为HTTPS已经做了，从而增强处理效率
        OmindAppEntity sysAppEntity = appService.getApp(signRequest.getAppkey());
        if(sysAppEntity == null){
            throw new BaseException("签名(APP)验证失败");
        }
        long tm = signRequest.getTm();
        long nowTm = System.currentTimeMillis() / 1000;

        if(Math.abs(tm - nowTm) > MAX_SIGN_TIMEOUT){
            throw new BaseException("签名(TM)验证失败");
        }
        String md5OriStr = "appkey=" + signRequest.getAppkey()
                + "&noncecode=" + signRequest.getNoncecode()
                + "&tm=" + signRequest.getTm()
                + sysAppEntity.getSecret();
        String md5Str = Md5Utils.getMD532Str(md5OriStr).toUpperCase();

        if(!md5Str.equals(signRequest.getSign())){
            throw new BaseException("签名校验失败");
        }

        //非获取uid场景（如登录验签）
        if(TextUtils.isBlank(token)){
            return 0L;
        }
        //其他场景顺便取下UID
        long uid =  selfUtil.token2Uid(token);
        if(uid > 0){
            signRequest.setOpUid(uid);
        }
        return uid;
    }


    public long token2Uid(String token){
        if(TextUtils.isBlank(token)){
            return 0;
        }
        String key = XcxRedisKey.USER_TOKEN + token;
        if(RedisUtils.hasKey(key)){
            return Long.valueOf(RedisUtils.getCacheObject(key).toString());
        }
        return 0L;
    }
}
