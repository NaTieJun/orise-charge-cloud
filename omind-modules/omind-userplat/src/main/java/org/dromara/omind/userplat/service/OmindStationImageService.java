package org.dromara.omind.userplat.service;

import org.dromara.omind.userplat.api.domain.entity.OmindStationImageEntity;

import java.util.List;

public interface OmindStationImageService {

    List<OmindStationImageEntity> all(String stationId);

}
