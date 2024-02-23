package org.dromara.omind.baseplat.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.omind.baseplat.api.domain.entity.SysStationPrice;
import org.dromara.omind.baseplat.domain.mapper.SysStationPriceMapper;
import org.dromara.omind.baseplat.domain.service.SysStationPriceIService;
import org.springframework.stereotype.Service;

@Service
public class SysStationPriceIServiceImpl extends ServiceImpl<SysStationPriceMapper, SysStationPrice> implements SysStationPriceIService {
}
