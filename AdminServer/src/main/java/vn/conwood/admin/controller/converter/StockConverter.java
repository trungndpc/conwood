package vn.conwood.admin.controller.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.conwood.admin.controller.dto.StockPromotionDTO;
import vn.conwood.admin.controller.form.PromotionForm;
import vn.conwood.admin.mapper.Mapper;
import vn.conwood.jpa.entity.promotion.StockPromotionEntity;

@Component
public class StockConverter {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Mapper mapper;

    public StockPromotionEntity convert2Entity(PromotionForm promotionForm) {
        return mapper.map(promotionForm, StockPromotionEntity.class);
    }

    public StockPromotionDTO convert2DTO(StockPromotionEntity stockPromotionEntity) {
        return mapper.map(stockPromotionEntity, StockPromotionDTO.class);
    }

}
