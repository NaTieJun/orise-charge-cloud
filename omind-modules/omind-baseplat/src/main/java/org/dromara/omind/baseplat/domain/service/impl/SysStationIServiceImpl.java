package org.dromara.omind.baseplat.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.omind.baseplat.api.domain.entity.SysStation;
import org.dromara.omind.baseplat.domain.mapper.SysStationMapper;
import org.dromara.omind.baseplat.domain.service.SysStationIService;
import org.springframework.stereotype.Service;

@Service
public class SysStationIServiceImpl extends ServiceImpl<SysStationMapper, SysStation> implements SysStationIService {
}
