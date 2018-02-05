package entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: yangx
 * @date: 2018/1/22
 * @description:
 */
public class SimpleOnuOlt implements Serializable{
    private Long id;
    private String oltDownId;
    private Date time;
    private Double upUtilizationRatio;

    public SimpleOnuOlt(Long id, String oltDownId, Date time, Double upUtilizationRatio) {
        this.id = id;
        this.oltDownId = oltDownId;
        this.time = time;
        this.upUtilizationRatio = upUtilizationRatio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOltDownId() {
        return oltDownId;
    }

    public void setOltDownId(String oltDownId) {
        this.oltDownId = oltDownId;
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
        return id + "," + oltDownId +"," + time + "," + upUtilizationRatio ;
    }
}
