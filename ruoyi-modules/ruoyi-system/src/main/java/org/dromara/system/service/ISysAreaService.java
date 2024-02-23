package org.dromara.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.system.domain.SysArea;
import org.dromara.system.domain.vo.SysAreaVo;

import java.util.List;

public interface ISysAreaService extends IService<SysArea> {

    List<SysArea> getList(SysArea sysArea);

    List<SysAreaVo> getChildListById(Integer id);

    public SysArea selectAreaById(Integer id);

}
