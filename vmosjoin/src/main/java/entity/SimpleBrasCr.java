package entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: yangx
 * @date: 2018/1/22
 * @description:
 */
public class SimpleBrasCr implements Serializable{
    private Long id;
    private String linkId;
    private Date time;
    private Double outUtilizationRatio;

    public SimpleBrasCr(Long id, String linkId, Date time, Double outUtilizationRatio) {
        this.id = id;
        this.linkId = linkId;
        this.time = time;
        this.outUtilizationRatio = outUtilizationRatio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Double getOutUtilizationRatio() {
        return outUtilizationRatio;
    }

    public void setOutUtilizationRatio(Double outUtilizationRatio) {
        this.outUtilizationRatio = outUtilizationRatio;
    }


    @Override
    public String toString() {
        return id + "," + linkId + "," + time + "," + outUtilizationRatio;
    }
}
