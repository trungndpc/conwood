package vn.conwood.jpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import vn.conwood.jpa.custom.FormRepositoryCustom;
import vn.conwood.jpa.entity.FormEntity;
import vn.conwood.jpa.entity.PromotionEntity;

import java.util.List;

public interface FormRepository extends JpaRepository<FormEntity, Integer>, JpaSpecificationExecutor<FormEntity>, FormRepositoryCustom {
    List<FormEntity> findByUserId(int userId);
    Page<FormEntity> findAll(Specification spec, Pageable pageable);

    List<FormEntity> findByPromotionId(int promotionId);

    @Query("SELECT DISTINCT userId FROM FormEntity")
    List<Integer> findDistinctUid();
}
