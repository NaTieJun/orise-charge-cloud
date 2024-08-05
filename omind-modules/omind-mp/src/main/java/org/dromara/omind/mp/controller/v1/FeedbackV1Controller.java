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
import org.dromara.omind.mp.token.annotation.TokenCheck;
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

    @TokenCheck
    @Operation(summary = "意见反馈")
    @PostMapping("feedback")
    public R feedback(@RequestBody FeedbackRequest feedbackRequest, SignRequest signRequest){
        try{
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

    @TokenCheck
    @Operation(summary = "意见反馈列表")
    @GetMapping("feedbackList")
    public TableDataInfo feedbackList(SignRequest signRequest, PageQuery pageQuery){
        try{

            TableDataInfo tableDataInfo = feedbackService.page(signRequest.getOpUid(), pageQuery);
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

    @TokenCheck
    @Operation(summary = "意见反馈详情")
    @GetMapping("info/{id}")
    public R info(@PathVariable Long id, SignRequest signRequest){
        try{
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
