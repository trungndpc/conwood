package vn.conwood.jpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.conwood.jpa.entity.PromotionEntity;

import java.util.List;

public interface PromotionRepository extends JpaRepository<PromotionEntity, Integer> {
    Page<PromotionEntity> findAll(Specification spec, Pageable pageable);
    List<PromotionEntity> findAllByOrderByUpdatedTimeDesc();
    List<PromotionEntity> findByTypeAndStatus(int type, int status);
}
