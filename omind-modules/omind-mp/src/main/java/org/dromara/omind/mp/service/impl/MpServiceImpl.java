package org.dromara.omind.mp.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.util.WxMaConfigHolder;
import lombok.extern.log4j.Log4j2;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.constant.HttpStatus;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.core.utils.ServletUtils;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.omind.api.common.utils.ip.IpUtils;
import org.dromara.omind.mp.constant.XcxRedisKey;
import org.dromara.omind.mp.domain.request.MpInfoRequest;
import org.dromara.omind.mp.domain.request.MpLoginRequest;
import org.dromara.omind.mp.domain.request.MpPhoneRequest;
import org.dromara.omind.mp.domain.vo.UserVo;
import org.dromara.omind.mp.service.MpService;
import org.dromara.omind.mp.utils.SignUtil;
import org.dromara.omind.userplat.api.domain.entity.OmindUserEntity;
import org.dromara.omind.userplat.api.service.RemoteOmindUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

@Log4j2
@Service
public class MpServiceImpl implements MpService {

    @Autowired
    WxMaService wxMaService;

    @Autowired
    SignUtil signUtil;

    @DubboReference
    RemoteOmindUserService userService;

    @Override
    public R loginByCode(String appid, MpLoginRequest loginRequest) {
        try {
            final String ip = IpUtils.getIpAddr(ServletUtils.getRequest());

            if (!wxMaService.switchover(appid)) {
                return R.fail("APPID无效");
            }

            WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(loginRequest.getCode());
            String unionId = session.getUnionid();
            String openId = session.getOpenid();
            if(TextUtils.isBlank(unionId)){
                return R.fail(HttpStatus.UNAUTHORIZED, "unionId获取失败");
            }
            OmindUserEntity userEntity = userService.getUserByUnionId(unionId);
            if(userEntity == null){
                userEntity = new OmindUserEntity();
                userEntity.setUnionIdWx(unionId);
                userEntity.setOpenIdWx(openId);
                userEntity.setLastVisitIp(ip);
                userEntity.setLastVisitTime(new Date());
                if(!userService.addUser(userEntity)){
                    return R.fail("用户注册失败");
                }
                //补充默认值
                userEntity = userService.getUserByUnionId(unionId);
            }
            else{
                userEntity.setLastVisitIp(ip);
                userEntity.setLastVisitTime(new Date());
                userService.updateById(userEntity);
            }
            if(userEntity.getDisableFlag() == 1){
                throw new BaseException("账号被禁用，请联系管理员");
            }
            String token = session.getSessionKey();
            RedisUtils.setCacheObject(XcxRedisKey.USER_TOKEN + token, userEntity.getUid(), Duration.ofDays(360));
            UserVo userVo = new UserVo();
            userVo.setToken(token);
            userVo.setUserInfo(userEntity);
            return R.ok(userVo);

        } catch (WxErrorException e) {
            log.error(e.getMessage(), e);
            return R.fail("登录失败");
        } finally {
            WxMaConfigHolder.remove();//清理ThreadLocal
        }
    }

    @Override
    public R phoneInfo(String appid, String token, MpPhoneRequest phoneRequest) {
        if (!wxMaService.switchover(appid)) {
            return R.fail("APPID无效");
        }
        try {
            Long uid = signUtil.token2Uid(token);
            String sessionKey = token;
            if(TextUtils.isBlank(sessionKey)){
                return R.fail("请重启小程序");
            }
            // 解密
            WxMaPhoneNumberInfo phoneNoInfo = wxMaService.getUserService().getPhoneNoInfo(sessionKey,
                    phoneRequest.getEncryptedData(), phoneRequest.getIv());
            WxMaConfigHolder.remove();//清理ThreadLocal


            OmindUserEntity omindUserEntity = userService.getUserById(uid);
            if (omindUserEntity == null) {
                return R.fail("用户信息获取失败");
            }
            if (TextUtils.isBlank(omindUserEntity.getNickName())) {
                omindUserEntity.setNickName(phoneNoInfo.getPurePhoneNumber());
            }
            omindUserEntity.setPhoneCode(phoneNoInfo.getCountryCode());
            omindUserEntity.setMobile(phoneNoInfo.getPurePhoneNumber());
            if (userService.updateById(omindUserEntity)) {
                if(!TextUtils.isBlank(omindUserEntity.getAvatar())){
                    //ldUserEntity.setAvatarUrl(minioS3Utils.getAccessUrl(ldUserEntity.getAvatar()));
                    //todo 文件
                }
                UserVo userVo = new UserVo();
                userVo.setToken(token);
                userVo.setUserInfo(omindUserEntity);
                return R.fail(userVo);
            } else {
                return R.fail("更新用户信息失败");
            }
        }
        catch (Exception ex){
            log.error(ex.toString());
            return R.fail("用户信息解析失败");
        }
    }

    @Override
    public R update(Long uid, MpInfoRequest mpInfoRequest) {
        if(uid == null || uid <= 0){
            return R.fail("未找到用户信息");
        }
        if(mpInfoRequest.getNickName() != null && mpInfoRequest.getNickName().length() > 16){
            return R.fail("昵称太长啦，请小于16个字符");
        }

        OmindUserEntity userEntity = userService.getUserById(uid);
        if(userEntity == null){
            return R.fail("未找到用户信息");
        }
        boolean isUpdate = false;
        if(!TextUtils.isBlank(mpInfoRequest.getNickName()) && !mpInfoRequest.getNickName().equals(userEntity.getNickName())){
            userEntity.setNickName(mpInfoRequest.getNickName());
            isUpdate = true;
        }
        if(!TextUtils.isBlank(mpInfoRequest.getAvatarUrl()) && !mpInfoRequest.getAvatarUrl().equals(userEntity.getAvatar())){
            userEntity.setAvatar(mpInfoRequest.getAvatarUrl());
            isUpdate = true;
        }
        if(isUpdate){
            userService.updateById(userEntity);
        }
        if(!TextUtils.isBlank(userEntity.getAvatar())){
            //userEntity.setAvatarUrl(minioS3Utils.getAccessUrl(ldUserEntity.getAvatar()));
            //todo 头像
        }
        return R.ok(userEntity);
    }

    @Override
    public R userInfo(Long uid) {
        if(uid == null || uid <= 0){
            return R.fail("用户未登录");
        }
        UserVo userVo = new UserVo();
        OmindUserEntity userInfo = userService.getUserById(uid);
        if(!TextUtils.isBlank(userInfo.getAvatar())){
            //userInfo.setAvatarUrl(minioS3Utils.getAccessUrl(userInfo.getAvatar()));
            //todo 头像
        }
        userVo.setUserInfo(userInfo);
        return R.ok(userVo);
    }
}
