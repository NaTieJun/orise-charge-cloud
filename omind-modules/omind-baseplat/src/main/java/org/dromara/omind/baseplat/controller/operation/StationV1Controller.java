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
import org.dromara.omind.baseplat.api.domain.dto.query.QuerySysStationDto;
import org.dromara.omind.baseplat.api.domain.entity.SysOperator;
import org.dromara.omind.baseplat.api.domain.entity.SysStation;
import org.dromara.omind.baseplat.service.SysOperatorService;
import org.dromara.omind.baseplat.service.SysStationService;
import org.dromara.omind.baseplat.service.notify.NotifyTradeInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "StationV1Controller", description = "[运营]基础服务层运营接口——充电站接口V1")
@RestController
@RequestMapping("/hlht/v1/sys/station")
public class StationV1Controller {

    @Autowired
    SysStationService stationService;

    @Autowired
    SysOperatorService operatorService;

    @Autowired
    NotifyTradeInfoService notifyTradeInfoService;

    @GetMapping(value = "/{stationId}")
    @Operation(summary = "根据充电站编号获取详细信息")
    public R getInfo(@PathVariable String stationId) {
        try {
            return R.ok(stationService.getStationById(stationId));
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

    @Operation(summary = "新增充电站")
    @PostMapping
    public R add(@Validated @RequestBody SysStation sysStation){
        try {
            if (TextUtils.isBlank(sysStation.getOperatorId())) {
                return R.fail(HttpStatus.ERROR, "运营商ID错误");
            }
            SysOperator sysOperator = operatorService.getOperatorById(sysStation.getOperatorId());
            if (sysOperator == null) {
                return R.fail(HttpStatus.ERROR, "未找到指定运营商");
            }
            if (!stationService.isStationIdValid(sysStation.getStationId())) {
                return R.fail(HttpStatus.ERROR, "充电站ID 错误/重复");
            }
            if(sysStation.getStationId().length() <= 3 || sysStation.getStationId().length() > 20){
                return R.fail(HttpStatus.ERROR, "充电站ID 小于等于20字符，大于3字符");
            }
            if (stationService.save(sysStation)) {
                return R.ok(sysStation);
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

    @Operation(summary = "修改充电站信息")
    @PutMapping
    public R edit(@Validated @RequestBody SysStation sysStation){
        try {
            return R.ok(stationService.updateById(sysStation));
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

    @DeleteMapping("/{stationId}")
    @Operation(summary = "删除充电站")
    public R remove(@PathVariable(value = "stationId", required = true) String stationId){
        try {
            SysStation sysStation = stationService.getStationById(stationId);
            if (sysStation == null) {
                return R.fail(HttpStatus.NOT_FOUND, "未找到待删除充电站");
            }
            if (stationService.remove(sysStation)) {
                return R.ok(sysStation);
            } else {
                return R.fail("数据更新失败");
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

    @GetMapping("/page")
    @Operation(summary = "获取充电站列表")
    public TableDataInfo page(QuerySysStationDto querySysStationDto,PageQuery pageQuery){
        try {
            TableDataInfo tableDataInfo = stationService.getStationPageList(querySysStationDto, pageQuery);
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
            log.error(ex.toString(), ex);
            TableDataInfo rspData = new TableDataInfo();
            rspData.setCode(HttpStatus.ERROR);
            rspData.setMsg("内部服务错误");
            return rspData;
        }
    }

    @GetMapping("/tree")
    @Operation(summary = "获取充电站树")
    public R tree(){
        try{
            return R.ok(stationService.allTree());
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

    @GetMapping("/gunsShow/{stationId}")
    @Operation(summary = "获取站级-枪监控列表")
    public R gunsShow(@PathVariable String stationId){
        try{
            return R.ok(stationService.getGunsShow(stationId));
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

}
