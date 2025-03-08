package org.dromara.omind.mp.service;

import org.dromara.common.core.domain.R;
import org.dromara.omind.mp.domain.request.MpInfoRequest;
import org.dromara.omind.mp.domain.request.MpLoginRequest;
import org.dromara.omind.mp.domain.request.MpMobileLoginRequest;
import org.dromara.omind.mp.domain.request.MpPhoneRequest;
import org.dromara.omind.mp.domain.vo.UserVo;

public interface MpService {

    public R loginByCode(String appid, MpLoginRequest loginRequest);

    public R phoneInfo(String appid, String token, MpPhoneRequest phoneRequest);

    public R update(Long uid, MpInfoRequest xcxInfoRequest);

    public R userInfo(Long uid);

    public R<UserVo> mobileLogin(MpMobileLoginRequest mpMobileLoginRequest);

}
