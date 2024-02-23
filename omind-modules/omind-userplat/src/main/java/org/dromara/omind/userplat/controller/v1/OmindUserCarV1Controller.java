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
import org.dromara.omind.userplat.api.domain.dto.OmindUserCarCheckDto;
import org.dromara.omind.userplat.api.domain.dto.OmindUserCarListDto;
import org.dromara.omind.userplat.service.OmindUserCarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

@Tag(name = "OmindUserCarV1Controller", description = "用户车辆接口")
@RestController
@RequestMapping(value = "/omind/v1/user/car")
@Slf4j
public class OmindUserCarV1Controller {

    @Autowired
    @Lazy
    OmindUserCarService omindUserCarService;

    /**
     * 管理后台查询用户车辆列表(分页)
     */
    @Operation( summary = "[OK]管理后台用户车辆列表")
    @GetMapping("/list")
    public TableDataInfo list(OmindUserCarListDto omindUserCarListDto, PageQuery pageQuery) {
        try {

            TableDataInfo rspData = new TableDataInfo();
            //字段验证
            String msg = omindUserCarService.validateUserCarListlFields(omindUserCarListDto);
            if (!TextUtils.isBlank(msg)) {
                rspData.setCode(HttpStatus.ERROR);
                rspData.setMsg(msg);
                return rspData;
            }

            return omindUserCarService.selectUserCarList(omindUserCarListDto,pageQuery);
        } catch (BaseException ex) {
            TableDataInfo rspData = new TableDataInfo();
            rspData.setCode(HttpStatus.ERROR);
            rspData.setMsg(ex.getMessage());
            return rspData;
        } catch (Exception ex) {
            log.error("userCar-list-error", ex);
            TableDataInfo rspData = new TableDataInfo();
            rspData.setCode(HttpStatus.ERROR);
            rspData.setMsg("内部服务错误");
            return rspData;
        }
    }

    /**
     * 管理后台根据车辆id获取详细信息
     */
    @Operation(summary = "[OK]管理后台根据车辆id获取详细信息")
    @GetMapping(value = "/{id}")
    public R getInfo(@PathVariable Long id) {
        try {
            return R.ok(omindUserCarService.info(id));
        } catch (BaseException ube) {
            return R.fail(ube.getCode(), ube.getMessage());
        } catch (Exception e) {
            log.error("userCar-info-error", e);
            return R.fail(500, "服务器内部错误");
        }
    }

    @Operation(summary = "[OK]管理后台用户车辆审核")
    @PutMapping(value = "/check")
    public R checkUserCar(@RequestBody OmindUserCarCheckDto omindUserCarCheckDto) {
        try {
            omindUserCarService.checkUserCar(omindUserCarCheckDto);
            return R.ok();
        } catch (BaseException ube) {
            return R.fail(ube.getCode(), ube.getMessage());
        } catch (Exception e) {
            log.error("userCar-checkUserCar-error", e);
            return R.fail(500, "服务器内部错误");
        }
    }
}
