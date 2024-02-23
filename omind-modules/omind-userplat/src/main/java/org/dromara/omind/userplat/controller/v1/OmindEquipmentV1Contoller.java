package org.dromara.omind.userplat.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.constant.HttpStatus;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.omind.userplat.service.OmindEquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "OmindEquipmentV1Controller", description = "充电桩接口")
@RestController
@RequestMapping(value = "/omind/v1/equipment")
@Slf4j
public class OmindEquipmentV1Contoller {

    @Autowired
    @Lazy
    OmindEquipmentService omindEquipmentService;

    /**
     * 查询充电桩编码列表(分页)
     */
    @Operation(summary = "充电设备编码列表")
    @GetMapping("/list/{stationId}")
    public TableDataInfo page(@PathVariable String stationId, PageQuery pageQuery)
    {
        try {
            TableDataInfo tabbleInfo = omindEquipmentService.selectEquipmentList(stationId,pageQuery);

            return tabbleInfo;
        } catch (BaseException ex) {
            TableDataInfo rspData = new TableDataInfo();
            rspData.setCode(HttpStatus.ERROR);
            rspData.setMsg(ex.getMessage());
            return rspData;
        } catch (Exception ex) {
            log.error("feedback-list-error", ex);
            TableDataInfo rspData = new TableDataInfo();
            rspData.setCode(HttpStatus.ERROR);
            rspData.setMsg("内部服务错误");
            return rspData;
        }
    }

    /**
     * 根据充电桩编码id获取详细信息
     */
    @Operation(summary = "根据充电桩编码id获取详细信息")
    @GetMapping(value = "/{equipmentId}")
    public R getInfo(@PathVariable String equipmentId) {
        try {
            return R.ok(omindEquipmentService.get(equipmentId));
        } catch (BaseException ube) {
            return R.fail(ube.getCode(), ube.getMessage());
        } catch (Exception e) {
            log.error("equipment-info-error", e);
            return R.fail(500, "服务器内部错误");
        }
    }

}
