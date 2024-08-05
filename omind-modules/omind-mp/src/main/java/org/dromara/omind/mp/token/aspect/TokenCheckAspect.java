package org.dromara.omind.mp.token.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.util.TextUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.dromara.common.core.constant.HttpStatus;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.omind.mp.domain.request.SignRequest;
import org.dromara.omind.mp.token.annotation.TokenCheck;
import org.dromara.omind.mp.utils.SignUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * @author : flainsky
 * @version : V0.0
 * @data : 2024/8/1 19:10
 * @company : ucode
 * @email : 298542443@qq.com
 * @title :
 * @Description :
 */
@Log4j2
@Aspect
@Component
public class TokenCheckAspect {

    @Autowired
    @Lazy
    SignUtil signUtil;

    @Pointcut("@annotation(tokenCheck)")
    public void tokenCheckPointCut(TokenCheck tokenCheck){}

    @Around("tokenCheckPointCut(tokenCheck)")
    public Object doBefore(ProceedingJoinPoint joinPoint, TokenCheck tokenCheck) {
        try {
            //获取RequestAttributes
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            //从获取RequestAttributes中获取HttpServletRequest的信息
            HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
            String token = request.getHeader("Token");
            if (TextUtils.isBlank(token)) {
                return R.fail(HttpStatus.UNAUTHORIZED,"token为空");
            }

            Object[] args = joinPoint.getArgs();
            SignRequest signRequest = null;
            if (ArrayUtils.isNotEmpty(args)){
                for (Object arg : args) {
                    //对象类型的入参直接修改属性即可
                    if (arg instanceof SignRequest){
                        signRequest= (SignRequest) arg;
                    }
                }
            }
            if(signRequest == null){
                return R.fail("签名错误");
            }

            if(tokenCheck.isSign()){
                long uid = signUtil.checkTokenAndSign(token, signRequest);
                if(uid <= 0){
                    return R.fail(HttpStatus.UNAUTHORIZED, "token无效");
                }
                else{
                    signRequest.setOpUid(uid);
                }
            }
            else{
                long uid = signUtil.token2Uid(token);
                if(uid <= 0){
                    return R.fail(HttpStatus.UNAUTHORIZED, "token已失效");
                }
                else{
                    signRequest.setOpUid(uid);
                }
            }
            Object result = joinPoint.proceed();
            return result;
        }
        catch (BaseException ex){
            log.error(ex.getMessage(), ex);
            return R.fail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        }
        catch (Exception ex){
            log.error(ex.getMessage(), ex);
            return R.fail("内部服务错误");
        }
        catch (Throwable ex){
            log.error(ex.getMessage(), ex);
            return R.fail("内部服务错误");
        }
    }

}
