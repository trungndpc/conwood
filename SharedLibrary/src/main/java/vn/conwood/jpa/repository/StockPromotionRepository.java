package vn.conwood.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.conwood.jpa.entity.promotion.StockPromotionEntity;

public interface StockPromotionRepository extends JpaRepository<StockPromotionEntity, Integer> {
}
