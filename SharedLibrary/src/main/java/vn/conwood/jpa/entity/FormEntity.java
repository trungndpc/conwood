package vn.conwood.jpa.entity;

import com.vladmihalcea.hibernate.type.array.ListArrayType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import vn.conwood.jpa.entity.base.BaseEntity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "form", schema = "public")
@TypeDef(name = "list-array",typeClass = ListArrayType.class)
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class FormEntity extends BaseEntity {
    @Id
    @GeneratedValue
    private Integer id;
    private Integer promotionId;
    private Integer userId;
    private int status;
    private int type;
    private String note;

    @Type(type = "list-array")
    @Column(name = "gift_ids",columnDefinition = "integer[]")
    private List<Integer> gifts;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Integer promotionId) {
        this.promotionId = promotionId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<Integer> getGifts() {
        return gifts;
    }

    public void setGifts(List<Integer> gifts) {
        this.gifts = gifts;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}
