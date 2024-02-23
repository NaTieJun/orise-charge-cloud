package org.dromara.omind.baseplat.controller.operation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.dromara.common.core.constant.HttpStatus;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.omind.baseplat.api.domain.dto.SysChargeOrderDto;
import org.dromara.omind.baseplat.api.domain.dto.query.QuerySysChargeOrderDto;
import org.dromara.omind.baseplat.api.domain.request.QuerySysChargeOrderAioDto;
import org.dromara.omind.baseplat.service.SysChargeOrderService;
import org.dromara.omind.baseplat.service.SysConnectorService;
import org.dromara.omind.baseplat.service.SysOperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "ChargeOrderV1Controller", description = "[运营]基础服务层运营接口——全量订单V1")
@Log4j2
@RestController
@RequestMapping("/hlht/v1/sys/charge/order")
public class ChargeOrderV1Controller {

    @Autowired
    SysChargeOrderService chargeOrderService;

    @Autowired
    @Lazy
    SysConnectorService connectorService;

    @Autowired
    @Lazy
    SysOperatorService operatorService;

    @GetMapping(value = "/{startChargeSeq}")
    @Operation(summary = "根据充电订单序列号获取充电订单信息")
    public R getInfo(@PathVariable String startChargeSeq) {
        try {
            return R.ok(chargeOrderService.getChargeOrderByStartChargeSeq(startChargeSeq));
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

    @GetMapping("/page")
    @Operation(summary = "分页获取全量充电订单")
    public TableDataInfo list(QuerySysChargeOrderDto querySysEquipmentDto, PageQuery pageQuery){
        try {
            TableDataInfo<SysChargeOrderDto> tableDataInfo = chargeOrderService.getChargeOrderList(querySysEquipmentDto, pageQuery);
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
            log.error(ex.toString());
            TableDataInfo rspData = new TableDataInfo();
            rspData.setCode(HttpStatus.ERROR);
            rspData.setMsg("内部服务错误");
            return rspData;
        }
    }

    @GetMapping("/page4Post")
    @Operation(summary = "分页获取全量充电订单")
    public TableDataInfo list4Post(@Validated QuerySysChargeOrderAioDto querySysEquipmentDto, PageQuery pageQuery){
        try {
            QuerySysChargeOrderDto querySysChargeOrderDto = new QuerySysChargeOrderDto();
            querySysChargeOrderDto.setConnectorId(querySysEquipmentDto.getConnectorId());
            querySysChargeOrderDto.setOperatorId(querySysEquipmentDto.getOperatorId());
            querySysChargeOrderDto.setStationId(querySysEquipmentDto.getStationId());
            querySysChargeOrderDto.setStartChargeSeq(querySysEquipmentDto.getStartChargeSeq());
            querySysChargeOrderDto.setStartChargeSeqStat(querySysEquipmentDto.getStartChargeSeqStat());
            querySysChargeOrderDto.setStartTm(querySysEquipmentDto.getStartTm());
            querySysChargeOrderDto.setEndTm(querySysEquipmentDto.getEndTm());
            querySysChargeOrderDto.setCarVin(querySysEquipmentDto.getCarVin());
            querySysChargeOrderDto.setPlateNum(querySysEquipmentDto.getPlateNum());
            querySysChargeOrderDto.setPhoneNum(querySysEquipmentDto.getPhoneNum());
            TableDataInfo<SysChargeOrderDto> pageInfo = chargeOrderService.getChargeOrderList(querySysChargeOrderDto, pageQuery);
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

}
