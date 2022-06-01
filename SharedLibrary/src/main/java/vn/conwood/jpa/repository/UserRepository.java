package vn.conwood.jpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.conwood.jpa.custom.UserRepositoryCustom;
import vn.conwood.jpa.entity.UserEntity;



public interface UserRepository extends JpaRepository<UserEntity, Integer>, JpaSpecificationExecutor<UserEntity>, UserRepositoryCustom {
    UserEntity findByPhone(String phone);
    UserEntity findByZaloId(String zaloId);
    UserEntity findByFollowerId(String followerId);
    Page<UserEntity> findAll(Specification spec, Pageable pageable);
}
