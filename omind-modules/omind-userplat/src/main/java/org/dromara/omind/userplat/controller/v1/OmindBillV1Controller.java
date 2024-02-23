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
import org.dromara.omind.userplat.api.domain.request.BillForceStopRequest;
import org.dromara.omind.userplat.api.domain.request.BillPageRequest;
import org.dromara.omind.userplat.service.OmindBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

@Tag(name = "BillV1Controller", description = "充电订单接口")
@RestController
@RequestMapping(value = "/omind/v1/bill")
@Slf4j
public class OmindBillV1Controller {

    @Autowired
    @Lazy
    OmindBillService omindBillService;

    /**
     * 查询充电订单列表(分页)
     */
    @Operation(summary = "充电订单列表")
    @GetMapping("/page")
    public TableDataInfo list(BillPageRequest billPageRequest, PageQuery pageQuery) {

        try {

            TableDataInfo rspData = new TableDataInfo();
            //字段验证
            String msg = omindBillService.validateBillListlFields(billPageRequest);
            if (!TextUtils.isBlank(msg)) {
                rspData.setCode(HttpStatus.ERROR);
                rspData.setMsg(msg);
                return rspData;
            }
            return omindBillService.getBillList(billPageRequest,pageQuery);
        } catch (BaseException ex) {
            TableDataInfo rspData = new TableDataInfo();
            rspData.setCode(HttpStatus.ERROR);
            rspData.setMsg(ex.getMessage());
            return rspData;
        } catch (Exception ex) {
            log.error("bill-list-error", ex);
            TableDataInfo rspData = new TableDataInfo();
            rspData.setCode(HttpStatus.ERROR);
            rspData.setMsg("内部服务错误");
            return rspData;
        }

    }

    /**
     * 根据充电订单id获取详细信息
     */
    @Operation(summary = "根据充电订单id获取详细信息")
    @GetMapping(value = "/{id}")
    public R getInfo(@PathVariable Long billId) {

        try {
            return R.ok(omindBillService.selectBillInfoById(billId));
        } catch (BaseException ube) {
            return R.fail(ube.getCode(), ube.getMessage());
        } catch (Exception e) {
            log.error("bill-info-error", e);
            return R.fail(500, "服务器内部错误");
        }

    }

    @Operation(summary = "强制停止充电并结算")
    @PostMapping("/forceStopBill")
    public R forceStopBill(@RequestBody BillForceStopRequest billForceStopRequest) {
        try {
            omindBillService.forceStopBill(billForceStopRequest);
            return R.ok();
        } catch (BaseException ube) {
            return R.fail(ube.getCode(), ube.getMessage());
        } catch (Exception e) {
            log.error("bill-stopBill-error", e);
            return R.fail(500, "服务器内部错误");
        }
    }

    /**
     * 根据充电订单序列号获取详细信息
     */
    @Operation(summary = "根据充电序列号获取详细信息")
    @GetMapping(value = "/seq/{startChargeSeq}")
    public R getBillInfo(@PathVariable String startChargeSeq) {
        try {
            return R.ok(omindBillService.get(startChargeSeq));
        } catch (BaseException ube) {
            return R.fail(ube.getCode(), ube.getMessage());
        } catch (Exception e) {
            log.error("bill-info-error", e);
            return R.fail(500, "服务器内部错误");
        }
    }

}
