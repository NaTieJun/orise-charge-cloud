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
import org.dromara.omind.userplat.api.domain.dto.OmindOperationListDto;
import org.dromara.omind.userplat.api.domain.entity.OmindOperatorEntity;
import org.dromara.omind.userplat.service.OmindOperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "OmindOperatorV1Controller", description = "基础运营商接口")
@RestController
@RequestMapping(value = "/omind/v1/operator")
@Slf4j
public class OmindOperatorV1Controller {

    @Autowired
    @Lazy
    OmindOperatorService omindOperatorService;

    /**
     * 查询基础运营商列表(分页)
     */
    @Operation(summary = "基础运营商列表")
    @GetMapping("/page")
    public TableDataInfo list(OmindOperationListDto omindOperationListDto, PageQuery pageQuery) {
        try {
            TableDataInfo tableDataInfo = omindOperatorService.selectOperatorInfoList(omindOperationListDto, pageQuery);
            return tableDataInfo;
        } catch (BaseException ex) {
            TableDataInfo rspData = new TableDataInfo();
            rspData.setCode(HttpStatus.ERROR);
            rspData.setMsg(ex.getMessage());
            return rspData;
        } catch (Exception ex) {
            log.error("operator--list-error", ex);
            TableDataInfo rspData = new TableDataInfo();
            rspData.setCode(HttpStatus.ERROR);
            rspData.setMsg("内部服务错误");
            return rspData;
        }
    }

    /**
     * 根据充基础运营商id获取详细信息
     */
    @Operation(summary = "根据基础运营商id获取详细信息")
    @GetMapping(value = "/{operatorId}")
    public R getInfo(@PathVariable String operatorId) {
        try {
            return R.ok(omindOperatorService.selectOperatorInfoById(operatorId));
        } catch (BaseException ube) {
            return R.fail(ube.getCode(),ube.getMessage());
        } catch (Exception e) {
            log.error("operator--info-error", e);
            return R.fail(500,"服务器内部错误");
        }
    }

    /**
     * 新增基础运营商
     */
    @Operation(summary = "新增基础运营商")
    @PostMapping
    public R add(@Validated @RequestBody OmindOperatorEntity omindOperatorEntity) {
        try {
            String msg = omindOperatorService.validateOperatorFields(omindOperatorEntity);
            if (!TextUtils.isBlank(msg)) {
                return R.fail(500, msg);
            }
            return R.ok(omindOperatorService.insertOperatorInfo(omindOperatorEntity));
        } catch (BaseException ube) {
            return R.fail(ube.getCode(), ube.getMessage());
        } catch (Exception e) {
            log.error("operator--add-error", e);
            return R.fail(500, "服务器内部错误");
        }
    }

    /**
     * 修改基础运营商
     */
    @Operation(summary = "修改基础运营商")
    @PutMapping
    public R edit(@Validated @RequestBody OmindOperatorEntity omindOperatorEntity) {

        try {
            String msg = omindOperatorService.validateOperatorFields(omindOperatorEntity);
            if (!TextUtils.isBlank(msg)) {
                return R.fail(500, msg);
            }
            return R.ok(omindOperatorService.updateOperatorInfo(omindOperatorEntity));
        } catch (BaseException ube) {
            return R.fail(ube.getCode(), ube.getMessage());
        } catch (Exception e) {
            log.error("operator--edit-error", e);
            return R.fail(500, "服务器内部错误");
        }

    }

    @Operation(summary = "重置密钥")
    @PutMapping("/{operatorId}")
    public R resetSecret(@PathVariable(value = "operatorId", required = true) String operatorId) {

        try {
            return R.ok(omindOperatorService.resetSecret(operatorId));
        } catch (BaseException ube) {
            return R.fail(ube.getCode(), ube.getMessage());
        } catch (Exception e) {
            log.error("operator--resetSecret-error", e);
            return R.fail(500, "服务器内部错误");
        }

    }

    /**
     * 删除基础运营商
     */
    @Operation(summary = "删除基础运营商")
    @DeleteMapping("/{operatorId}")
    public R remove(@PathVariable String operatorId) {

        try {
            omindOperatorService.deleteOperatorInfoById(operatorId);
            return R.ok();
        } catch (BaseException ube) {
            return R.fail(ube.getCode(), ube.getMessage());
        } catch (Exception e) {
            return R.fail(500, "服务器内部错误");
        }

    }

}
