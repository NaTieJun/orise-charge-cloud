package org.dromara.omind.mp.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.constant.HttpStatus;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.omind.mp.domain.request.SignRequest;
import org.dromara.omind.mp.utils.SignUtil;
import org.dromara.omind.userplat.api.domain.entity.OmindFeedbackEntity;
import org.dromara.omind.userplat.api.domain.request.FeedbackRequest;
import org.dromara.omind.userplat.api.service.RemoteOmindFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "FeedbackV1Controller", description = "意见反馈")
@RestController
@RequestMapping(value = "/v1/feedback/")
public class FeedbackV1Controller {

    @Autowired
    HttpServletRequest request;

    @Autowired
    SignUtil signUtil;

    @DubboReference
    RemoteOmindFeedbackService feedbackService;

    @Operation(summary = "意见反馈")
    @PostMapping("feedback")
    public R feedback(@RequestBody FeedbackRequest feedbackRequest, SignRequest signRequest){
        try{
            String token = request.getHeader("token");
            if(TextUtils.isBlank(token)){
                return R.fail(HttpStatus.UNAUTHORIZED, "未登录");
            }

            Long uid = signUtil.checkTokenAndSign(token, signRequest);

            feedbackService.feedback(feedbackRequest, signRequest.getOpUid());
            return R.ok("ok");
        }
        catch (BaseException ex){
            log.error(ex.toString());
            return R.fail(ex.getMessage());
        }
        catch (Exception ex){
            log.error(ex.toString());
            return R.fail("内部服务错误");
        }
    }

    @Operation(summary = "意见反馈列表")
    @GetMapping("feedbackList")
    public TableDataInfo feedbackList(SignRequest signRequest, PageQuery pageQuery){
        try{
            String token = request.getHeader("token");
            if(TextUtils.isBlank(token)){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.UNAUTHORIZED);
                rspData.setMsg("未登录");
                return rspData;
            }

            Long uid = signUtil.checkTokenAndSign(token, signRequest);
            log.info("feedback--uid=" + uid + "|signRequest=" + signRequest);

            TableDataInfo tableDataInfo = feedbackService.page(uid, pageQuery);
            return tableDataInfo;
        }
        catch (BaseException ex){
            log.error(ex.toString());
            TableDataInfo rspData = new TableDataInfo();
            rspData.setCode(HttpStatus.ERROR);
            rspData.setMsg(ex.getMessage());
            return rspData;
        }
        catch (Exception ex){
            log.error(ex.toString());
            TableDataInfo rspData = new TableDataInfo();
            rspData.setCode(HttpStatus.ERROR);
            rspData.setMsg("内部服务错误");
            return rspData;
        }
    }

    @Operation(summary = "意见反馈详情")
    @GetMapping("info/{id}")
    public R info(@PathVariable Long id, SignRequest signRequest){
        try{
            String token = request.getHeader("token");
            if(TextUtils.isBlank(token)){
                return R.fail(HttpStatus.UNAUTHORIZED, "未登录");
            }

            signUtil.checkTokenAndSign(token, signRequest);

            OmindFeedbackEntity feedbackEntity = feedbackService.info(id);
            return R.ok(feedbackEntity);
        }
        catch (BaseException ex){
            log.error(ex.toString());
            return R.fail(ex.getMessage());
        }
        catch (Exception ex){
            log.error(ex.toString());
            return R.fail("内部服务错误");
        }
    }

}
