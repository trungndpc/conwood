package vn.conwood.jpa.custom;

import org.springframework.data.jpa.domain.Specification;
import vn.conwood.jpa.entity.UserEntity;
import vn.conwood.jpa.metric.UserCityMetric;
import vn.conwood.jpa.metric.UserDataMetric;

import java.util.List;

public interface UserRepositoryCustom {
    List<UserCityMetric> statisticUserByCity();
    List<UserDataMetric> statisticUserByDate(List<Integer> statuses);
    List<UserEntity> findAllWithIdOnly(Specification<UserEntity> spec);
}
