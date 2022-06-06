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
}
