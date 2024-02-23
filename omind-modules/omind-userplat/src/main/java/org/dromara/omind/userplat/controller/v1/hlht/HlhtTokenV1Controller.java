package org.dromara.omind.userplat.controller.v1.hlht;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.omind.userplat.api.domain.dto.HlhtDto;
import org.dromara.omind.userplat.api.domain.dto.HlhtResult;
import org.dromara.omind.userplat.service.hlht.HlhtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RequestMapping(value = "/omind/evcs/v1/")
@RestController
@Tag(name = "HlhtTokenController", description = "互联互通认证接口")
public class HlhtTokenV1Controller {

    @Autowired
    @Lazy
    HlhtTokenService hlhtTokenService;

    @Operation(summary = "[OK]获取token")
    @PostMapping("query_token")
    public HlhtResult queryToken(@RequestBody HlhtDto hlhtDto){

        try {
            return hlhtTokenService.getToken(hlhtDto);
        } catch (BaseException ube) {
            log.error("queryToken-error", ube);
            return HlhtResult.error(ube.getCode(),ube.getMessage());
        } catch (Exception e) {
            log.error("queryToken-error", e);
            return HlhtResult.error("服务器内部错误");
        }
    }

}
