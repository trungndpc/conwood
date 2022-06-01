package vn.conwood.admin.controller.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import vn.conwood.admin.controller.dto.BroadcastDTO;
import vn.conwood.admin.controller.dto.PageDTO;
import vn.conwood.admin.controller.form.BroadcastForm;
import vn.conwood.admin.mapper.Mapper;
import vn.conwood.jpa.entity.BroadcastEntity;

import java.util.ArrayList;
import java.util.List;

@Component
public class BroadcastConverter {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Mapper mapper;

    public BroadcastEntity convert2Entity(BroadcastForm form) {
        return mapper.map(form, BroadcastEntity.class);
    }

    public BroadcastDTO convert2DTO(BroadcastEntity broadcastEntity) {
        return mapper.map(broadcastEntity, BroadcastDTO.class);
    }

    public PageDTO<BroadcastDTO> convertToPageDTO(Page<BroadcastEntity> broadcastEntities)  {
        List<BroadcastEntity> content = broadcastEntities.getContent();
        List<BroadcastDTO> dtos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(content)) {
            for (BroadcastEntity entity: content) {
                dtos.add(convert2DTO(entity));
            }
        }
        return new PageDTO<BroadcastDTO>(broadcastEntities.getNumber(), dtos.size(), broadcastEntities.getTotalPages(), dtos);
    }


}
