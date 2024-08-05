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
import org.dromara.omind.userplat.api.domain.request.ChargeOrderListRequest;
import org.dromara.omind.mp.utils.SignUtil;
import org.dromara.omind.userplat.api.domain.request.GetChargeOrderInfoRequest;
import org.dromara.omind.userplat.api.domain.request.StartChargeRequest;
import org.dromara.omind.userplat.api.domain.request.StopChargeRequest;
import org.dromara.omind.userplat.api.service.RemoteOmindChargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "ChargeV1Controller", description = "充电接口")
@RestController
@RequestMapping(value = "/v1/charge/")
public class ChargeV1Controller {

    @Autowired
    HttpServletRequest request;

    @Autowired
    SignUtil signUtil;

    @DubboReference
    RemoteOmindChargeService chargeService;

    @TokenCheck
    @Operation(summary = "启动充电")
    @PostMapping("startCharge")
    public R startCharge(@RequestBody StartChargeRequest startChargeRequest, SignRequest signRequest){
        try {
            return R.ok(chargeService.startCharge(startChargeRequest, signRequest.getOpUid()));
        }
        catch (BaseException ex){
            log.error(ex.toString(), ex);
            return R.fail(ex.getMessage());
        }
        catch (Exception ex){
            log.error(ex.toString(), ex);
            return R.fail("内部服务错误");
        }
    }

    @TokenCheck
    @Operation(summary = "手动结束充电")
    @PostMapping("stopCharge")
    public R stopCharge(@RequestBody StopChargeRequest stopChargeRequest, SignRequest signRequest){
        try {
            chargeService.stopCharge(stopChargeRequest.getStartChargeSeq(), signRequest.getOpUid());
            return R.ok();
        }
        catch (BaseException ex){
            log.error(ex.toString(), ex);
            return R.fail(ex.getMessage());
        }
        catch (Exception ex){
            log.error(ex.toString(), ex);
            return R.fail("内部服务错误");
        }
    }

    @TokenCheck
    @Operation(summary = "获取充电订单列表")
    @GetMapping("getChargeOrderList")
    public TableDataInfo getChargeOrderList(ChargeOrderListRequest chargeOrderListRequest, SignRequest signRequest, PageQuery pageQuery){
        try {
            TableDataInfo dataTable = chargeService.chargeOrderList(chargeOrderListRequest, signRequest.getOpUid(), pageQuery);
            return dataTable;
        }
        catch (BaseException ex){
            log.error(ex.toString(), ex);
            TableDataInfo rspData = new TableDataInfo();
            rspData.setCode(HttpStatus.ERROR);
            rspData.setMsg(ex.getMessage());
            return rspData;
        }
        catch (Exception ex){
            log.error(ex.toString(), ex);
            TableDataInfo rspData = new TableDataInfo();
            rspData.setCode(HttpStatus.ERROR);
            rspData.setMsg("内部服务错误");
            return rspData;
        }
    }

    @TokenCheck
    @Operation(summary = "获取充电订单详情")
    @GetMapping("getChargeOrderInfo")
    public R getChargeOrderInfo(GetChargeOrderInfoRequest getChargeOrderInfoRequest, SignRequest signRequest){
        try {
            return R.ok(chargeService.chargeOrderInfo(getChargeOrderInfoRequest.getStartChargeSeq()));
        }
        catch (BaseException ex){
            log.error(ex.toString(), ex);
            return R.fail(ex.getMessage());
        }
        catch (Exception ex){
            log.error(ex.toString(), ex);
            return R.fail("内部服务错误");
        }
    }

}
