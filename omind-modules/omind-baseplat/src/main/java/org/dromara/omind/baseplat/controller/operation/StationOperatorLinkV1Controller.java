package org.dromara.omind.baseplat.controller.operation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.omind.baseplat.api.domain.request.LinkStationOperatorRequest;
import org.dromara.omind.baseplat.api.domain.request.LinkStationOperatorUpdateRequest;
import org.dromara.omind.baseplat.service.StationOperatorLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name ="StationOperatorLinkV1Controller", description = "[运营]基础服务层运营接口——充电站&运营商关联接口V1")
@RestController
@RequestMapping("/hlht/v1/sys/station/link/operator/")
public class StationOperatorLinkV1Controller {

    @Autowired
    StationOperatorLinkService linkService;

    @GetMapping(value = "/list4OperatorId/{operatorId}")
    @Operation(summary = "根据运营商编号获取运营站点列表")
    public R list4OperatorId(@PathVariable String operatorId) {
        try {
            return R.ok(linkService.getList4OperatorId(operatorId));
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

    @GetMapping(value = "/list4StationId/{stationId}")
    @Operation(summary = "根据充电站编号获取运营商列表")
    public R list4StationId(@PathVariable String stationId) {
        try {
            return R.ok(linkService.getList4Station(stationId));
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

    @Operation(summary = "新增关联")
    @PostMapping
    public R addLink(@Validated @RequestBody LinkStationOperatorRequest linkStationOperatorRequest){
        try {
            return R.ok(linkService.linkOperatorAndStation(linkStationOperatorRequest));
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

    @Operation(summary = "修改关联信息")
    @PutMapping
    public R updateLink(@Validated @RequestBody LinkStationOperatorUpdateRequest updateRequest){
        try {
            return R.ok(linkService.updateLink(updateRequest));
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

    @DeleteMapping("/{id}")
    @Operation(summary = "删除关联")
    public R remove(@PathVariable(value = "id", required = true)Long id){
        try {
            return R.ok(linkService.delLink(id));
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
