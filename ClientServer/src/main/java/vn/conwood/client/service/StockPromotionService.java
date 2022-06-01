package vn.conwood.client.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.conwood.common.status.StatusPromotion;
import vn.conwood.common.type.TypePromotion;
import vn.conwood.jpa.entity.PromotionEntity;
import vn.conwood.jpa.entity.UserEntity;
import vn.conwood.jpa.repository.PromotionRepository;
import vn.conwood.jpa.specification.PromotionSpecification;

import java.util.List;
import java.util.Optional;

@Service
public class StockPromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private PromotionSpecification promotionSpecification;

    public  PromotionEntity find(UserEntity userEntity) {
        long currentTime = System.currentTimeMillis();
        List<PromotionEntity> promotionEntityList = promotionRepository.findByTypeAndStatus(TypePromotion.STOCK_PROMOTION_TYPE,
                StatusPromotion.APPROVED);
        Optional<PromotionEntity> first = promotionEntityList.stream().filter(e -> {
                    if (e.getDistrictIds() != null) {
                        return e.getDistrictIds().contains(userEntity.getDistrictId());
                    }
                    if (e.getCityIds() != null) {
                        return e.getCityIds().contains(userEntity.getCityId());
                    }
                    return true;
                }).filter(e -> e.getTimeStart() <= currentTime)
                .filter(e -> currentTime <= e.getTimeEnd())
                .findFirst();
        if (!first.isPresent()) {
            return null;
        }
        return first.get();
    }
}
