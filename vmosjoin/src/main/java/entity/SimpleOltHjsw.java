package entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: yangx
 * @date: 2018/1/22
 * @description:
 */
public class SimpleOltHjsw implements Serializable{
    private Long id;
    private String oltUpId;
    private Date time;
    private Double upUtilizationRatio;

    public SimpleOltHjsw(Long id, String oltUpId, Date time, Double upUtilizationRatio) {
        this.id = id;
        this.oltUpId = oltUpId;
        this.time = time;
        this.upUtilizationRatio = upUtilizationRatio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOltUpId() {
        return oltUpId;
    }

    public void setOltUpId(String oltUpId) {
        this.oltUpId = oltUpId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Double getUpUtilizationRatio() {
        return upUtilizationRatio;
    }

    public void setUpUtilizationRatio(Double upUtilizationRatio) {
        this.upUtilizationRatio = upUtilizationRatio;
    }

    @Override
    public String toString() {
        return id + "," + oltUpId +"," + time + "," + upUtilizationRatio;
    }
}
