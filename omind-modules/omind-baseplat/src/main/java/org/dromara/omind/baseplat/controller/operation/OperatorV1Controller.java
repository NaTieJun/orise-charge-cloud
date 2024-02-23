package org.dromara.omind.baseplat.controller.operation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.constant.HttpStatus;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.omind.api.common.utils.SecretMaker;
import org.dromara.omind.baseplat.api.domain.dto.query.QuerySysOperatorDto;
import org.dromara.omind.baseplat.api.domain.entity.SysOperator;
import org.dromara.omind.baseplat.service.SysOperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "OperatorV1Controller", description = "[运营]基础服务层运营接口——运营商接口V1")
@Slf4j
@RestController
@RequestMapping("/hlht/v1/sys/operator")
public class OperatorV1Controller {

    @Autowired
    SysOperatorService operatorService;


    @GetMapping(value = "/{operatorId}")
    @Operation(summary = "根据运营商编号获取详细信息")
    public R getInfo(@PathVariable String operatorId) {
        try {
            return R.ok(operatorService.getOperatorById(operatorId));
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

    @Operation(summary = "新增运营商")
    @PostMapping
    public R add(@Validated @RequestBody SysOperator sysOperator){

        try {
            if (!operatorService.isOperatorIdValid(sysOperator.getOperatorId())) {
                if (TextUtils.isBlank(sysOperator.getOperatorId()) || sysOperator.getOperatorId().length() != 9) {
                    return R.fail(HttpStatus.ERROR, "运营商ID（组织机构代码）必须为9位");
                } else {
                    return R.fail(HttpStatus.ERROR, "运营商ID（组织机构代码）重复");
                }
            }
            sysOperator.setOperatorSecret(SecretMaker.getRandom32HSecretMaker());
            sysOperator.setDataSecret(SecretMaker.getRandom16SecretMaker());
            sysOperator.setDataSecretIv(SecretMaker.getRandom16SecretMaker());
            sysOperator.setSigSecret(SecretMaker.getRandom32HSecretMaker());
            if (operatorService.save(sysOperator)) {
                return R.ok(sysOperator);
            } else {
                return R.fail("新增失败");
            }
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

    @Operation(summary = "修改运营商")
    @PutMapping
    public R edit(@Validated @RequestBody SysOperator sysOperator){

        try {
            if (sysOperator.getId() == null || sysOperator.getId() <= 0) {
                return R.fail("ID错误");
            }
            return R.ok(operatorService.updateById(sysOperator));
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

    @Operation(summary = "重置密钥")
    @PutMapping("/{operatorId}")
    public R resetSecret(@PathVariable(value = "operatorId", required = true) String operatorId){
        try {
            if (TextUtils.isBlank(operatorId)) {
                return R.fail("ID错误");
            }
            SysOperator sysOperator = operatorService.getOperatorById(operatorId);
            if (sysOperator == null || sysOperator.getDelFlag() == 1) {
                return R.fail("运营商不存在");
            }
            SysOperator updateObj = new SysOperator();
            updateObj.setUpdateById(sysOperator.getId());
            updateObj.setOperatorSecret(SecretMaker.getRandom32HSecretMaker());
            updateObj.setDataSecret(SecretMaker.getRandom16SecretMaker());
            updateObj.setDataSecretIv(SecretMaker.getRandom16SecretMaker());
            updateObj.setSigSecret(SecretMaker.getRandom32HSecretMaker());
            return R.ok(operatorService.updateById(updateObj));
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

    @Operation(summary = "获取运营商列表")
    @GetMapping("/page")
    public TableDataInfo list(QuerySysOperatorDto querySysOperatorDto, PageQuery pageQuery){
        try {
            TableDataInfo<SysOperator> pageInfo = operatorService.getOperatorPageList(querySysOperatorDto, pageQuery);
            return pageInfo;
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

    @DeleteMapping("/{operatorId}")
    @Operation(summary = "删除运营商")
    public R remove(@PathVariable(value = "operatorId", required = true)String operatorId){
        try {
            SysOperator sysOperator = operatorService.getOperatorById(operatorId);
            if (sysOperator == null) {
                return R.fail(HttpStatus.NOT_FOUND, "未找到待删除充电站");
            }
            if (operatorService.remove(sysOperator)) {
                return R.ok(sysOperator);
            } else {
                return R.fail("数据更新失败");
            }
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
