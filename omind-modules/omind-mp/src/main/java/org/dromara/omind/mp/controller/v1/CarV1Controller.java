package org.dromara.omind.mp.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.constant.HttpStatus;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.omind.mp.domain.request.SignRequest;
import org.dromara.omind.mp.token.annotation.TokenCheck;
import org.dromara.omind.mp.utils.SignUtil;
import org.dromara.omind.userplat.api.domain.dto.OmindUserCarInsertDto;
import org.dromara.omind.userplat.api.domain.dto.OmindUserCarUpdateDto;
import org.dromara.omind.userplat.api.service.RemoteOmindUserCarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "CarV1Controller", description = "车辆相关")
@RestController
@RequestMapping(value = "/v1/car/")
public class CarV1Controller {

    @Autowired
    HttpServletRequest request;

    @Autowired
    SignUtil signUtil;

    @DubboReference
    RemoteOmindUserCarService userCarService;

    @TokenCheck
    @Operation(summary = "[OK]获取用户车辆列表")
    @GetMapping("list")
    public R list(SignRequest signRequest){
        try {
            return R.ok(userCarService.list(signRequest.getOpUid()));
        }
        catch (BaseException ex){
            log.error("userCar-list-error",ex);
            return R.fail(ex.getMessage());
        }
        catch (Exception ex){
            log.error("userCar-list-error",ex);
            return R.fail("内部服务错误");
        }
    }

    @TokenCheck
    @Operation(summary = "[OK]车辆信息详情")
    @GetMapping("info/{id}")
    public R info(@PathVariable Long id, SignRequest signRequest){
        try {
            return R.ok(userCarService.info(id));
        }
        catch (BaseException ex){
            log.error("userCar-info-error",ex);
            return R.fail(ex.getMessage());
        }
        catch (Exception ex){
            log.error("userCar-info-error",ex);
            return R.fail("内部服务错误");
        }
    }

    @TokenCheck
    @Operation(summary = "[OK]添加用户车辆")
    @PostMapping("insert")
    public R insert(@RequestBody OmindUserCarInsertDto omindUserCarInsertDto, SignRequest signRequest){
        try {
            omindUserCarInsertDto.setUserId(signRequest.getOpUid());
            userCarService.insert(omindUserCarInsertDto);
            return R.ok();
        }
        catch (BaseException ex){
            log.error("userCar-insert-error",ex);
            return R.fail(ex.getMessage());
        }
        catch (Exception ex){
            log.error("userCar-insert-error",ex);
            return R.fail("内部服务错误");
        }
    }

    @TokenCheck
    @Operation(summary = "[OK]修改用户车辆")
    @PostMapping("upcar")
    public R upcar(@RequestBody OmindUserCarUpdateDto omindUserCarUpdateDto, SignRequest signRequest){
        try {
            omindUserCarUpdateDto.setUserId(signRequest.getOpUid());
            userCarService.upCar(omindUserCarUpdateDto);
            return R.ok();
        }
        catch (BaseException ex){
            log.error("userCar-upcar-error",ex);
            return R.fail(ex.getMessage());
        }
        catch (Exception ex){
            log.error("userCar-upcar-error",ex);
            return R.fail("内部服务错误");
        }
    }

    @TokenCheck
    @Operation(summary = "[OK]删除用户车辆")
    @PutMapping("del/{id}")
    public R del(@PathVariable Long id, SignRequest signRequest){
        try {
            userCarService.del(id);
            return R.ok();
        }
        catch (BaseException ex){
            log.error("userCar-del-error",ex);
            return R.fail(ex.getMessage());
        }
        catch (Exception ex){
            log.error("userCar-del-error",ex);
            return R.fail("内部服务错误");
        }
    }

}
