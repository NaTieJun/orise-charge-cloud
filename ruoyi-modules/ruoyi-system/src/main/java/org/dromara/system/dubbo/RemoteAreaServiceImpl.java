package org.dromara.system.dubbo;

import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.system.api.RemoteAreaService;
import org.dromara.system.api.domain.vo.RemoteAreaVo;
import org.dromara.system.domain.SysArea;
import org.dromara.system.service.ISysAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;


@Service
@DubboService
public class RemoteAreaServiceImpl implements RemoteAreaService {

    @Autowired
    @Lazy
    ISysAreaService areaService;

    @Override
    public RemoteAreaVo getByAreaCode(Integer areaCode) {
        SysArea area = areaService.selectAreaById(areaCode);
        if(area == null){
            return null;
        }
        RemoteAreaVo remoteAreaVo = new RemoteAreaVo();
        remoteAreaVo.setId(area.getId());
        remoteAreaVo.setLevel(area.getLevel());
        remoteAreaVo.setParentId(area.getParentId());
        remoteAreaVo.setName(area.getName());
        remoteAreaVo.setEPrefix(area.getEPrefix());
        remoteAreaVo.setEName(area.getEName());
        remoteAreaVo.setExtId(area.getExtId());
        remoteAreaVo.setExtName(area.getExtName());

        return remoteAreaVo;
    }
}
