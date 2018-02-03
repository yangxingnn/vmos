package entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: yangx
 * @date: 2018/1/22
 * @description:
 */
public class SimpleIptv implements Serializable{
    private Long id;
    private String customerId;
    private Double vmos;
    private Date time;

    public SimpleIptv(Long id, String customerId, Double vmos, Date time) {
        this.id = id;
        this.customerId = customerId;
        this.vmos = vmos;
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Double getVmos() {
        return vmos;
    }

    public void setVmos(Double vmos) {
        this.vmos = vmos;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return id + "," + customerId +"," + vmos + "," + time;
    }
}
