package org.dromara.omind.mp.service;

import org.dromara.common.core.domain.R;
import org.dromara.omind.mp.domain.request.MpInfoRequest;
import org.dromara.omind.mp.domain.request.MpLoginRequest;
import org.dromara.omind.mp.domain.request.MpPhoneRequest;

public interface MpService {

    public R loginByCode(String appid, MpLoginRequest loginRequest);

    public R phoneInfo(String appid, String token, MpPhoneRequest phoneRequest);

    public R update(Long uid, MpInfoRequest xcxInfoRequest);

    public R userInfo(Long uid);

}
