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
import org.dromara.omind.baseplat.api.domain.dto.query.QuerySysConnectorDto;
import org.dromara.omind.baseplat.api.domain.entity.SysConnector;
import org.dromara.omind.baseplat.api.domain.entity.SysEquipment;
import org.dromara.omind.baseplat.service.SysConnectorService;
import org.dromara.omind.baseplat.service.SysEquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name ="ConnectorV1Controller", description = "[运营]基础服务层运营接口——充电（枪）接口V1")
@Slf4j
@RestController
@RequestMapping("/hlht/v1/sys/connector")
public class ConnectorV1Controller {

    @Autowired
    SysEquipmentService equipmentService;

    @Autowired
    SysConnectorService connectorService;


    @GetMapping(value = "/{connectorId}")
    @Operation(summary = "根据充电接口编号获取详细信息")
    public R getInfo(@PathVariable String connectorId) {

        try {
            return R.ok(connectorService.getConnectorById(connectorId));
        }
        catch (BaseException ex){
            log.error(ex.toString());
            return R.fail(ex.getMessage());
        }
        catch (Exception ex){
            log.error("根据充电接口编号获取详细信息失败", ex);
            return R.fail("内部服务错误");
        }
    }

    @Operation(summary = "新增充电接口（枪）")
    @PostMapping
    public R add(@Validated @RequestBody SysConnector sysConnector){
        try {
            if (TextUtils.isBlank(sysConnector.getConnectorId())) {
                return R.fail(HttpStatus.ERROR, "充电接口ID错误");
            }
            SysEquipment sysEquipment = equipmentService.getEquipmentById(sysConnector.getEquipmentId());
            if (sysEquipment == null) {
                return R.fail(HttpStatus.ERROR, "未找到指定充电设备");
            }
            if (!connectorService.isConnectorIdValid(sysConnector.getConnectorId())) {
                return R.fail(HttpStatus.ERROR, "充电接口ID 错误/重复");
            }
            if(sysConnector.getConnectorId().length() < 10 || sysConnector.getConnectorId().length() > 26){
                return R.fail(HttpStatus.ERROR, "充电接口ID长度为10至26的字符串");
            }
            if (connectorService.add(sysConnector)) {
                return R.ok(sysConnector);
            } else {
                return R.fail("添加数据失败");
            }
        }
        catch (BaseException ex){
            log.error(ex.toString());
            return R.fail(ex.getMessage());
        }
        catch (Exception ex){
            log.error(ex.toString(), ex);
            return R.fail("内部服务错误");
        }
    }

    @Operation(summary = "修改充电接口信息")
    @PutMapping
    public R edit(@Validated @RequestBody SysConnector sysConnector){

        try {
            return R.ok(connectorService.updateById(sysConnector));
        }
        catch (BaseException ex){
            log.error(ex.toString());
            return R.fail(ex.getMessage());
        }
        catch (Exception ex){
            log.error(ex.toString(),ex);
            return R.fail("内部服务错误");
        }
    }

    @DeleteMapping("/{connectId}")
    @Operation(summary = "删除充电接口")
    public R remove(@PathVariable(value = "connectId", required = true)String connectId){
        try {
            SysConnector sysConnector = connectorService.getConnectorById(connectId);
            if (sysConnector == null) {
                return R.fail(HttpStatus.NOT_FOUND, "未找到待删除充电接口");
            }
            if (connectorService.remove(sysConnector)) {
                return R.ok(sysConnector);
            } else {
                return R.fail("数据更新失败");
            }
        }
        catch (BaseException ex){
            log.error(ex.toString());
            return R.fail(ex.getMessage());
        }
        catch (Exception ex){
            log.error(ex.toString(),ex);
            return R.fail("内部服务错误");
        }
    }

    @GetMapping("/page")
    @Operation(summary = "获取充电接口列表")
    public TableDataInfo list(QuerySysConnectorDto querySysConnectorDto, PageQuery pageQuery){
        try {
            TableDataInfo<SysConnector> pageInfo = connectorService.getConnectorPageList(querySysConnectorDto, pageQuery);
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
            log.error(ex.toString(), ex);
            TableDataInfo rspData = new TableDataInfo();
            rspData.setCode(HttpStatus.ERROR);
            rspData.setMsg("内部服务错误");
            return rspData;
        }
    }

}
