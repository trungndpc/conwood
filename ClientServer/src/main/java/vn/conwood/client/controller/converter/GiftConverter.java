package vn.conwood.client.controller.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.conwood.jpa.entity.GiftEntity;
import vn.conwood.client.controller.dto.GiftDTO;
import vn.conwood.client.mapper.Mapper;


@Component
public class GiftConverter {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Mapper mapper;

    public GiftDTO convert2DTO(GiftEntity giftEntity) {
        return mapper.map(giftEntity, GiftDTO.class);
    }


}
