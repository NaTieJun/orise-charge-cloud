package org.dromara.system.api;

import org.dromara.system.api.domain.vo.RemoteAreaVo;

public interface RemoteAreaService {

    RemoteAreaVo getByAreaCode(Integer areaCode);

}
