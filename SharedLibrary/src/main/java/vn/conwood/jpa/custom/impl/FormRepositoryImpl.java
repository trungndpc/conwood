package vn.conwood.jpa.custom.impl;

import org.springframework.stereotype.Repository;
import vn.conwood.jpa.custom.FormRepositoryCustom;
import vn.conwood.jpa.entity.FormEntity;
import vn.conwood.jpa.entity.FormEntity_;
import vn.conwood.jpa.metric.FormCityMetric;
import vn.conwood.jpa.metric.FormDateMetric;
import vn.conwood.jpa.metric.FormPromotionMetric;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class FormRepositoryImpl implements FormRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<FormDateMetric> statisticFormByDate(List<Integer> promotions) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<FormEntity> root = query.from(FormEntity.class);
        Expression<Date> dateExpression = root.get(FormEntity_.createdTime).as(Date.class);
        if (promotions != null) {
            query.where(root.get(FormEntity_.promotionId).in(promotions));
        }
        query.groupBy(dateExpression);
        query.orderBy(cb.desc(dateExpression));
        query.multiselect(dateExpression, cb.count(root.get(FormEntity_.id)));
        TypedQuery<Object[]> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(0);
        typedQuery.setMaxResults(15);
        List<Object[]> resultList = typedQuery.getResultList();
        return resultList.stream().map(r -> new FormDateMetric(r[0].toString(), Integer.parseInt(r[1].toString())))
                .collect(Collectors.toList());
    }

    @Override
    public List<FormCityMetric> statisticFormByCity(List<Integer> promotions) {
        Query query = entityManager.createNativeQuery("select u.city_id, count(f.id) " +
                "from form f " +
                "inner join \"public\".user u ON f.user_id = u.id " +
                "group by (u.city_id)");
        List<Object[]> resultList = query.getResultList();
        return resultList.stream().map(r -> new FormCityMetric(Integer.parseInt(r[0].toString()),
                        Integer.parseInt(r[1].toString())))
                .collect(Collectors.toList());
    }

    @Override
    public List<FormPromotionMetric> statisticFormByPromotion() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<FormEntity> root = query.from(FormEntity.class);
        Expression<Integer> expression = root.get(FormEntity_.PROMOTION_ID);
        Expression<Long> count = cb.count(root.get(FormEntity_.id));
        query.groupBy(expression);
        query.multiselect(expression, count);
        query.orderBy(cb.desc(count));
        TypedQuery<Object[]> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(0);
        typedQuery.setMaxResults(15);
        List<Object[]> resultList = typedQuery.getResultList();
        return resultList.stream().map(r -> new FormPromotionMetric(Integer.parseInt(r[0].toString()), Integer.parseInt(r[1].toString())))
                .collect(Collectors.toList());
    }

}
