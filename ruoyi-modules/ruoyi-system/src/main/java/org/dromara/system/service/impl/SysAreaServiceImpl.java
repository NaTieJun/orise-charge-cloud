package org.dromara.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.system.domain.SysArea;
import org.dromara.system.domain.vo.SysAreaVo;
import org.dromara.system.mapper.SysAreaMapper;
import org.dromara.system.service.ISysAreaService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysAreaServiceImpl extends ServiceImpl<SysAreaMapper, SysArea> implements ISysAreaService {

    @Override
    public List<SysArea> getList(SysArea sysArea) {
        LambdaQueryWrapper<SysArea> wrapper = new LambdaQueryWrapper<>();
        if(sysArea.getId() != null){
            wrapper.eq(SysArea::getId,sysArea.getId());
        }
        if(sysArea.getParentId() != null){
            wrapper.eq(SysArea::getParentId,sysArea.getParentId());
        }
        if(sysArea.getName() != null){
            wrapper.like(SysArea::getName,sysArea.getName());
        }
        if(sysArea.getEPrefix() != null){
            wrapper.eq(SysArea::getEPrefix,sysArea.getEPrefix());
        }
        wrapper.eq(SysArea::getDelFlag,0);
        return list(wrapper);
    }

    @Override
    public List<SysAreaVo> getChildListById(Integer id) {
        List<SysArea> list = list(new LambdaQueryWrapper<SysArea>()
                .eq(SysArea::getParentId, id)
                .eq(SysArea::getDelFlag, 0)
                .orderByAsc(SysArea::getExtId));
        return list.stream().map(sysArea -> {
            SysAreaVo sysAreaVo = new SysAreaVo();
            BeanUtil.copyProperties(sysArea,sysAreaVo);
            return sysAreaVo;
        }).collect(Collectors.toList());
    }

    @Override
    public SysArea selectAreaById(Integer id) {
        return getById(id);
    }
}
