package org.dromara.omind.mp.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.constant.HttpStatus;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.omind.mp.domain.request.*;
import org.dromara.omind.mp.domain.vo.UserVo;
import org.dromara.omind.mp.service.MpService;
import org.dromara.omind.mp.utils.SignUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "AuthV1Controller", description = "用户认证")
@RestController
@RequestMapping(value = "/v1/auth/")
public class AuthV1Controller {

    @Autowired
    HttpServletRequest request;

    @Autowired
    SignUtil signUtil;

    @Autowired
    MpService mpService;


    @Operation(summary = "小程序登录接口")
    @PostMapping("login/{appid}")
    public R xcxLogin(@PathVariable String appid, @RequestBody MpLoginRequest request, SignRequest signRequest)
    {
        try {
            if (StringUtils.isBlank(request.getCode())) {
                return R.fail("code为空");
            }
            //签名验证
            signUtil.checkTokenAndSign(null, signRequest);
            return mpService.loginByCode(appid, request);
        }
        catch (BaseException ex){
            log.error(ex.toString());
            return R.fail(ex.getMessage());
        }
        catch (Exception ex){
            log.error("小程序登录接口", ex);
            return R.fail("内部服务错误");
        }
    }

    @Operation(summary = "小程序更新用户信息")
    @PostMapping("update")
    public R update(@RequestBody MpInfoRequest infoRequest, SignRequest signRequest){
        try {
            String token = request.getHeader("token");
            if (TextUtils.isBlank(token)) {
                return R.fail("TOKEN无效");
            }
            Long uid = signUtil.checkTokenAndSign(token, signRequest);
            return mpService.update(uid, infoRequest);
        }
        catch (BaseException ex){
            log.error(ex.toString());
            return R.fail(ex.getMessage());
        }
        catch (Exception ex){
            log.error("小程序更新用户信息",ex);
            return R.fail("内部服务错误");
        }
    }

    @Operation(summary = "小程序关联用户手机号（消费业务前置必要条件）")
    @PostMapping("phone/{appid}")
    public R phone(@PathVariable String appid, @RequestBody MpPhoneRequest phoneRequest, SignRequest signRequest) {

        try {
            String token = request.getHeader("token");
            if (TextUtils.isBlank(token)) {
                return R.fail("TOKEN无效");
            }
            signUtil.checkTokenAndSign(token, signRequest);

            return mpService.phoneInfo(appid, token, phoneRequest);
        }
        catch (BaseException ex){
            log.error(ex.toString());
            return R.fail(ex.getMessage());
        }
        catch (Exception ex){
            log.error("小程序关联用户手机号",ex);
            return R.fail("内部服务错误");
        }
    }

    @Operation(summary = "根据token查询用户信息")
    @GetMapping("info")
    public R info(SignRequest signRequest){
        try {
            String token = request.getHeader("token");
            if (TextUtils.isBlank(token)) {
                return R.fail("TOKEN失效");
            }
            Long uid = signUtil.checkTokenAndSign(token, signRequest);
            return mpService.userInfo(uid);
        }
        catch (BaseException ex){
            log.error(ex.toString());
            return R.fail(ex.getMessage());
        }
        catch (Exception ex){
            log.error("根据token查询用户信息",ex);
            return R.fail("内部服务错误");
        }
    }

    @Operation(summary = "支付宝小程序登录接口")
    @PostMapping("ali/login")
    public R<UserVo> aliLogin(@RequestBody AliLoginRequest request, SignRequest signRequest) {
        try {
            //签名验证
            signUtil.checkTokenAndSign(null, signRequest);
            //todo 完成阿里登录
            //mpService.aliLogin(request);
            return null;
        } catch (BaseException ex) {
            log.error(ex.toString(), ex);
            return R.fail(HttpStatus.ERROR, ex.getMessage());
        } catch (Exception ex) {
            log.error("小程序登录接口授权失败", ex);
            return R.fail(HttpStatus.ERROR, "内部服务错误");
        }
    }

    @Operation(summary = "支付宝小程序手机号解析接口")
    @PostMapping("ali/phone")
    public R<UserVo> aliPhone(@RequestBody AliPhoneRequest aliPhoneRequest, SignRequest signRequest) {
        try {
            String token = request.getHeader("token");
            if (TextUtils.isBlank(token)) {
                return R.fail(HttpStatus.ERROR,"TOKEN无效");
            }
            signUtil.checkTokenAndSign(token, signRequest);

            aliPhoneRequest.setToken(token);

            //todo  完成阿里登录
            //return mpService.aliPhoneInfo(aliPhoneRequest);
            return null;
        } catch (BaseException ex) {
            log.error(ex.toString(), ex);
            return R.fail(HttpStatus.ERROR, ex.getMessage());
        } catch (Exception ex) {
            log.error("小程序手机号解析接口失败", ex);
            return R.fail(HttpStatus.ERROR, "内部服务错误");
        }
    }

    @Operation(summary = "手机号直接登录（自动注册）")
    @PostMapping("login/phone")
    public R<UserVo> mobileLogin(@RequestBody MpMobileLoginRequest mpMobileLoginRequest, SignRequest signRequest){
        try {
            signUtil.checkTokenAndSign(null, signRequest);

            if(TextUtils.isBlank(mpMobileLoginRequest.getMobile())){
                return R.fail("无效的手机号");
            }
            else if(TextUtils.isBlank(mpMobileLoginRequest.getVerCode())){
                return R.fail("无效的验证码");
            }

            //todo return mpService.mobileLogin(mpMobileLoginRequest);
            return null;
        }
        catch (BaseException ex){
            log.error(ex.toString(), ex);
            return R.fail(HttpStatus.ERROR, ex.getMessage());
        }
        catch (Exception ex){
            log.error("手机号直接登录",ex);
            return R.fail(HttpStatus.ERROR, "内部服务错误");
        }
    }

}
