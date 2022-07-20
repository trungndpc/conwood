package vn.conwood.jpa.entity.form;


import vn.conwood.jpa.entity.FormEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "stock_form", schema = "public")
public class StockFormEntity extends FormEntity {
    private String jsonImage;
    private Integer bags;
    private String address;
    private String phone;

    private Integer value;

    private String storeName;

    public String getJsonImage() {
        return jsonImage;
    }

    public void setJsonImage(String jsonImage) {
        this.jsonImage = jsonImage;
    }

    public Integer getBags() {
        return bags;
    }

    public void setBags(Integer bags) {
        this.bags = bags;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
}
