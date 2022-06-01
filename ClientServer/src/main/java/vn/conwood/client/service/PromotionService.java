package vn.conwood.client.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.conwood.common.status.StatusPromotion;
import vn.conwood.jpa.entity.PromotionEntity;
import vn.conwood.jpa.entity.UserEntity;
import vn.conwood.jpa.repository.PromotionRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    public List<PromotionEntity> findPromotion(int type, UserEntity userEntity) {
        List<PromotionEntity> promotionEntities = promotionRepository.findAllByOrderByUpdatedTimeDesc();
        return promotionEntities.stream().filter(promotion -> promotion.getType() == type)
                .filter(promotion -> promotion.getStatus() == StatusPromotion.APPROVED)
                .filter(promotion -> {
                    if (promotion.getCityIds() != null) {
                        return promotion.getCityIds().contains(userEntity.getCityId());
                    }
                    return true;
                })
                .filter(promotion -> {
                    if (promotion.getDistrictIds() != null) {
                        return promotion.getDistrictIds().contains(userEntity.getDistrictId());
                    }
                    return true;
                }).collect(Collectors.toList());
    }

    public PromotionEntity get(int id) {
        return promotionRepository.getOne(id);
    }
}
