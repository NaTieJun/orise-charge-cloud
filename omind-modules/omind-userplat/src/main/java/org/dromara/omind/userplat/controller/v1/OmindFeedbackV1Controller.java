package org.dromara.omind.userplat.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.constant.HttpStatus;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.omind.userplat.api.domain.dto.OmindFeedbackListDto;
import org.dromara.omind.userplat.api.domain.entity.OmindFeedbackEntity;
import org.dromara.omind.userplat.service.OmindFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "OmindFeedbackV1Controller", description = "用户反馈信息接口")
@RestController
@RequestMapping("/omind/v1/feedback")
public class OmindFeedbackV1Controller {

    @Autowired
    @Lazy
    OmindFeedbackService omindFeedbackService;

    /**
     * 查询用户反馈列表(分页)
     */
    @Operation(summary = "[OK]用户反馈列表")
    @GetMapping("/list")
    public TableDataInfo list(OmindFeedbackListDto omindFeedbackListDto, PageQuery pageQuery) {
        try {
            TableDataInfo rspData = new TableDataInfo();
            //字段验证
            String msg = omindFeedbackService.validateFeedbackListlFields(omindFeedbackListDto);
            if (!TextUtils.isBlank(msg)) {
                rspData.setCode(HttpStatus.ERROR);
                rspData.setMsg(msg);
                return rspData;
            }
            TableDataInfo dataTable = omindFeedbackService.selectFeedbackList(omindFeedbackListDto,pageQuery);

            return dataTable;
        } catch (BaseException ex) {
            TableDataInfo rspData = new TableDataInfo();
            rspData.setCode(HttpStatus.ERROR);
            rspData.setMsg(ex.getMessage());
            return rspData;
        } catch (Exception ex) {
            log.error("feedback-list-error", ex);
            TableDataInfo rspData = new TableDataInfo();
            rspData.setCode(HttpStatus.ERROR);
            rspData.setMsg("内部服务错误");
            return rspData;
        }
    }

    /**
     * 根据用户反馈id获取详细信息
     */
    @Operation(summary = "[OK]根据用户反馈id获取详细信息")
    @GetMapping(value = "/{id}")
    public R getInfo(@PathVariable Long id) {
        try {
            return R.ok(omindFeedbackService.info(id));
        } catch (BaseException ube) {
            return R.fail(ube.getCode(), ube.getMessage());
        } catch (Exception e) {
            log.error("feedback-info-error", e);
            return R.fail(500, "服务器内部错误");
        }
    }

    /**
     * 回复用户反馈
     */
    @Operation(summary = "[OK]修改用反馈")
    @PutMapping
    public R edit(@Validated @RequestBody OmindFeedbackEntity odFeedback) {
        try {
            String msg = omindFeedbackService.validateFeedbackFields(odFeedback);
            if (!TextUtils.isBlank(msg)) {
                return R.fail(500, msg);
            }
            return R.ok(omindFeedbackService.updateFeedbackReply(odFeedback));
        } catch (BaseException ube) {
            return R.fail(ube.getCode(), ube.getMessage());
        } catch (Exception e) {
            log.error("feedback-edit-error", e);
            return R.fail(500, "服务器内部错误");
        }
    }

}
