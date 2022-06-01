package vn.conwood.jpa.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import vn.conwood.jpa.entity.GiftEntity;
import vn.conwood.jpa.entity.GiftEntity_;

@Component
public class GiftSpecification {

    public Specification<GiftEntity> isStatus(int status) {
        return (root, query, builder) -> builder.equal(root.get(GiftEntity_.status), status);
    }
}
