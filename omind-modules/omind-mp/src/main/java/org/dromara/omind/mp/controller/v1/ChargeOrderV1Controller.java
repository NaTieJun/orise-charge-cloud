package org.dromara.omind.mp.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.constant.HttpStatus;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.omind.mp.domain.request.SignRequest;
import org.dromara.omind.mp.service.PrepareOrderService;
import org.dromara.omind.mp.token.annotation.TokenCheck;
import org.dromara.omind.mp.utils.SignUtil;
import org.dromara.omind.userplat.api.domain.request.PrepareOrderInfoRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "ChargeOrderV1Controller", description = "充电订单")
@RestController
@RequestMapping(value = "/v1/charge/order/")
public class ChargeOrderV1Controller {

    @Autowired
    HttpServletRequest request;

    @Autowired
    SignUtil signUtil;

    @Autowired
    PrepareOrderService prepareOrderService;

    @TokenCheck
    @Operation(summary = "获取充电下单前展示信息详情")
    @GetMapping("prepareOrderInfo")
    public R prepareOrderInfo(PrepareOrderInfoRequest prepareOrderInfoRequest, SignRequest signRequest){

        try {
            if (signRequest.getOpUid() == null || signRequest.getOpUid() <= 0) {
                return R.fail(HttpStatus.UNAUTHORIZED, "未登录");
            }
            return R.ok(prepareOrderService.prepareOrderInfo(signRequest.getOpUid(), prepareOrderInfoRequest.getConnectorId(), prepareOrderInfoRequest.getHasPrice()));
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
