package vn.conwood.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.conwood.jpa.entity.BroadcastEntity;

public interface BroadcastRepository extends JpaRepository<BroadcastEntity, Integer> {
}
