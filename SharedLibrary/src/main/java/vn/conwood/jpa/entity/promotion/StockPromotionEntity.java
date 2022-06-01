package vn.conwood.jpa.entity.promotion;

import vn.conwood.jpa.entity.PromotionEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "stock_promotion", schema = "public")
public class StockPromotionEntity extends PromotionEntity {

}
