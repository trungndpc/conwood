package vn.conwood.client.controller.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import vn.conwood.jpa.entity.PromotionEntity;
import vn.conwood.client.controller.dto.PromotionDTO;
import vn.conwood.client.mapper.Mapper;


@Component
public class PromotionConverter {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Mapper mapper;

    public PromotionDTO convert2DTO(PromotionEntity promotionEntity) {
        return mapper.map(promotionEntity, PromotionDTO.class);
    }


}
