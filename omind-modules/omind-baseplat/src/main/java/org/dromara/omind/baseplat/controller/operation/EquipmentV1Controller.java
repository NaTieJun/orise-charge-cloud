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
import org.dromara.omind.baseplat.api.domain.dto.query.QuerySysEquipmentDto;
import org.dromara.omind.baseplat.api.domain.entity.SysEquipment;
import org.dromara.omind.baseplat.api.domain.entity.SysStation;
import org.dromara.omind.baseplat.service.SysEquipmentService;
import org.dromara.omind.baseplat.service.SysStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "EquipmentV1Controller", description = "[运营]基础服务层运营接口——充电设备（桩）接口V1")
@Slf4j
@RestController
@RequestMapping("/hlht/v1/sys/equipment")
public class EquipmentV1Controller {

    @Autowired
    SysStationService stationService;

    @Autowired
    SysEquipmentService equipmentService;

    @GetMapping(value = "/{equipmentId}")
    @Operation(summary = "根据充电设备编号获取详细信息")
    public R getInfo(@PathVariable String equipmentId) {
        try {
            return R.ok(equipmentService.getEquipmentById(equipmentId));
        }
        catch (BaseException ex){
            log.error(ex.toString());
            return R.fail(ex.getMessage(), ex);
        }
        catch (Exception ex){
            log.error(ex.toString(), ex);
            return R.fail("内部服务错误");
        }
    }

    @Operation(summary = "新增充电设备（桩）")
    @PostMapping
    public R add(@Validated @RequestBody SysEquipment sysEquipment){
        try {
            if (TextUtils.isBlank(sysEquipment.getEquipmentId())) {
                return R.fail(HttpStatus.ERROR, "充电设备ID错误");
            }
            SysStation sysStation = stationService.getStationById(sysEquipment.getStationId());
            if (sysStation == null) {
                return R.fail(HttpStatus.ERROR, "未找到指定充电站");
            }
            if (!equipmentService.isEquipmentIdValid(sysEquipment.getEquipmentId())) {
                return R.fail(HttpStatus.ERROR, "充电设备ID 错误/重复");
            }
            if(sysEquipment.getEquipmentId().length() > 23 || sysEquipment.getEquipmentId().length() < 10){
                return R.fail(HttpStatus.ERROR, "充电设备ID允许10-23位字符串");
            }
            if (equipmentService.save(sysEquipment)) {
                return R.ok(sysEquipment);
            } else {
                return R.fail("添加数据失败");
            }
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

    @Operation(summary = "修改充电设备信息")
    @PutMapping
    public R edit(@Validated @RequestBody SysEquipment sysEquipment){
        try {
            return R.ok(equipmentService.update(sysEquipment));
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

    @DeleteMapping("/{equipmentId}")
    @Operation(summary = "删除充电设备")
    public R remove(@PathVariable(value = "equipmentId", required = true)String equipmentId){
        try {
            SysEquipment sysEquipment = equipmentService.getEquipmentById(equipmentId);
            if (sysEquipment == null) {
                return R.fail(HttpStatus.NOT_FOUND, "未找到待删除充电设备");
            }
            if (equipmentService.remove(sysEquipment)) {
                return R.ok(sysEquipment);
            } else {
                return R.fail("数据更新失败");
            }
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

    @GetMapping("/page")
    @Operation(summary = "获取充电设备列表")
    public TableDataInfo list(QuerySysEquipmentDto querySysEquipmentDto, PageQuery pageQuery){
        try {
            TableDataInfo<SysEquipment> pageInfo = equipmentService.getEquipmentPageList(querySysEquipmentDto, pageQuery);
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
