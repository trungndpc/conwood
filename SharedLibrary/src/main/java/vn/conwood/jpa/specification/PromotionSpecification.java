package vn.conwood.jpa.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import vn.conwood.jpa.entity.PromotionEntity;
import vn.conwood.jpa.entity.PromotionEntity_;

import java.util.List;

@Component
public class PromotionSpecification {
    public Specification<PromotionEntity> inTypes(List<Integer> types) {
        return (root, query, builder) ->
                root.get(PromotionEntity_.type).in(types);
    }

    public Specification<PromotionEntity> isNotStatus(int status) {
        return (root, query, builder) -> builder.notEqual(root.get(PromotionEntity_.status), status);
    }
}
