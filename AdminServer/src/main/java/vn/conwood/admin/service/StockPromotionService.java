package vn.conwood.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.conwood.common.status.StatusPromotion;
import vn.conwood.jpa.entity.promotion.StockPromotionEntity;
import vn.conwood.jpa.repository.StockPromotionRepository;

@Service
public class StockPromotionService {

    @Autowired
    private StockPromotionRepository stockPromotionRepository;

    public StockPromotionEntity create(StockPromotionEntity entity) {
        entity.setStatus(StatusPromotion.INIT);
        return stockPromotionRepository.saveAndFlush(entity);
    }

    public StockPromotionEntity get(int id) {
        return stockPromotionRepository.getOne(id);
    }

}
