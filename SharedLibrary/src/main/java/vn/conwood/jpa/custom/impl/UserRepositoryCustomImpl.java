package vn.conwood.jpa.custom.impl;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import vn.conwood.common.status.StatusUser;
import vn.conwood.jpa.custom.UserRepositoryCustom;
import vn.conwood.jpa.entity.FormEntity_;
import vn.conwood.jpa.entity.UserEntity;
import vn.conwood.jpa.entity.UserEntity_;
import vn.conwood.jpa.metric.UserCityMetric;
import vn.conwood.jpa.metric.UserDataMetric;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<UserCityMetric> statisticUserByCity() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<UserEntity> root = query.from(UserEntity.class);
        query.groupBy(root.get(UserEntity_.cityId));
        query.multiselect(root.get(UserEntity_.cityId), cb.count(root.get(UserEntity_.id)));
//        query.where(cb.equal(root.get(UserEntity_.STATUS), StatusUser.APPROVED));
        List<Object[]> resultList = entityManager.createQuery(query).getResultList();
        return resultList.stream().filter(r -> r != null && r[0] != null && r[1] != null)
                .map(r -> new UserCityMetric(Integer.parseInt(r[0].toString()),
                Integer.parseInt(r[1].toString()))).collect(Collectors.toList());
    }

    @Override
    public List<UserDataMetric> statisticUserByDate(List<Integer> statuses) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<UserEntity> root = query.from(UserEntity.class);
        Expression<Date> dateExpression = root.get(UserEntity_.createdTime).as(Date.class);
        query.where(root.get(UserEntity_.status).in(statuses));
        query.groupBy(dateExpression);
        query.orderBy(cb.desc(dateExpression));
        query.multiselect(dateExpression, cb.count(root.get(UserEntity_.id)));
        TypedQuery<Object[]> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(0);
        typedQuery.setMaxResults(15);
        List<Object[]> resultList = typedQuery.getResultList();
        return resultList.stream().map(r -> new UserDataMetric(r[0].toString(), Integer.parseInt(r[1].toString())))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserEntity> findAllWithIdOnly(Specification<UserEntity> spec) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> tupleQuery = criteriaBuilder.createTupleQuery();
        Root<UserEntity> root = tupleQuery.from(UserEntity.class);
        tupleQuery.multiselect(getSelection(root, UserEntity_.id),
                getSelection(root, UserEntity_.name));
        if (spec != null) {
            tupleQuery.where(spec.toPredicate(root, tupleQuery, criteriaBuilder));
        }

        List<Tuple> CustomerNames = entityManager.createQuery(tupleQuery).getResultList();
        return createEntitiesFromTuples(CustomerNames);
    }

    private Selection<?> getSelection(Root<UserEntity> root,
                                      SingularAttribute<UserEntity, ?> attribute) {
        return root.get(attribute).alias(attribute.getName());
    }

    private List<UserEntity> createEntitiesFromTuples(List<Tuple> CustomerNames) {
        List<UserEntity> userEntities = new ArrayList<>();
        for (Tuple tuple : CustomerNames) {
            UserEntity userEntity = new UserEntity();
            userEntity.setId(tuple.get(UserEntity_.id.getName(), Integer.class));
            userEntities.add(userEntity);
        }
        return userEntities;
    }
}
