package vn.conwood.jpa.custom;

import vn.conwood.jpa.metric.*;

import java.util.List;

public interface FormRepositoryCustom {
    List<FormDateMetric> statisticFormByDate(List<Integer> promotions);
    List<FormCityMetric> statisticFormByCity(List<Integer> promotions);
    List<FormPromotionMetric> statisticFormByPromotion();

}
