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
import org.dromara.omind.userplat.api.domain.dto.OmindUserListDto;
import org.dromara.omind.userplat.api.domain.dto.OmindUserOptDto;
import org.dromara.omind.userplat.service.OmindUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

@Tag(name = "OmindUserV1Controller", description = "用户接口")
@RestController
@RequestMapping(value = "/omind/v1/user")
@Slf4j
public class OmindUserV1Controller {

    @Autowired
    @Lazy
    OmindUserService omindUserService;

    /**
     * 管理后台查询用户列表(分页)
     */
    @Operation( summary = "[OK]管理后台用户列表")
    @GetMapping("/page")
    public TableDataInfo list(OmindUserListDto omindUserListDto, PageQuery pageQuery) {
        try {

            TableDataInfo rspData = new TableDataInfo();
            //字段验证
            String msg = omindUserService.validateUserListlFields(omindUserListDto);
            if (!TextUtils.isBlank(msg)) {
                rspData.setCode(HttpStatus.ERROR);
                rspData.setMsg(msg);
                return rspData;
            }

            return omindUserService.selectUserList(omindUserListDto,pageQuery);
        } catch (BaseException ex) {
            TableDataInfo rspData = new TableDataInfo();
            rspData.setCode(HttpStatus.ERROR);
            rspData.setMsg(ex.getMessage());
            return rspData;
        } catch (Exception ex) {
            log.error("user-list-error", ex);
            TableDataInfo rspData = new TableDataInfo();
            rspData.setCode(HttpStatus.ERROR);
            rspData.setMsg("内部服务错误");
            return rspData;
        }
    }

    /**
     * 管理后台根据用户id获取详细信息
     */
    @Operation(summary = "[OK]管理后台根据用户id获取详细信息")
    @GetMapping(value = "/{userId}")
    public R getInfo(@PathVariable Long userId) {
        try {
            return R.ok(omindUserService.getUserById(userId));
        } catch (BaseException ube) {
            return R.fail(ube.getCode(), ube.getMessage());
        } catch (Exception e) {
            log.error("user-info-error", e);
            return R.fail(500, "服务器内部错误");
        }
    }

    @Operation(summary = "[OK]用户启用禁用")
    @PutMapping(value = "/opt")
    public R disableUser(@RequestBody OmindUserOptDto omindUserOptDto)
    {
        try {
            omindUserService.disableUser(omindUserOptDto);
            return R.ok();
        } catch (BaseException ube) {
            return R.fail(ube.getCode(), ube.getMessage());
        } catch (Exception e) {
            log.error("user-disableUser-error", e);
            return R.fail(500, "服务器内部错误");
        }
    }
}
