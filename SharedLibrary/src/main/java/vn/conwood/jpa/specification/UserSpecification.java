package vn.conwood.jpa.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import vn.conwood.jpa.entity.FormEntity_;
import vn.conwood.jpa.entity.UserEntity;
import vn.conwood.jpa.entity.UserEntity_;

import java.util.List;

@Component
public class UserSpecification {

    public Specification<UserEntity> isStatus(int status) {
        return (root, query, builder) ->
                builder.equal(root.get(UserEntity_.status), status);
    }

    public Specification<UserEntity> isCity(int location) {
        return (root, query, builder) ->
                builder.equal(root.get(UserEntity_.cityId), location);
    }

    public Specification<UserEntity> inCity(List<Integer> cityIds) {
        return (root, query, builder) ->
                root.get(UserEntity_.cityId).in(cityIds);
    }

    public Specification<UserEntity> inDistrict(List<Integer> districtIds) {
        return (root, query, builder) ->
                root.get(UserEntity_.districtId).in(districtIds);
    }

    public Specification<UserEntity> inStatus(List<Integer> statuses) {
        return (root, query, builder) ->
                root.get(UserEntity_.status).in(statuses);
    }


    public Specification<UserEntity> likePhone(String phone) {
        return (root, query, builder) ->
                builder.like(root.get(UserEntity_.phone), phone);
    }

    public Specification<UserEntity> likeName(String name) {
        return (root, query, builder) ->
                builder.like(root.get(UserEntity_.name), name);
    }

}
