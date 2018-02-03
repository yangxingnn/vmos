package entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: yangx
 * @date: 2018/1/15
 * @description:
 */
public class OnuOlt implements Serializable{
    private String id;
    private Double upBandwidth;
    private Double downBandwidth;
    private Double upRate;
    private Double downRate;
    private Double upUtilizationRatio;
    private Double downUtilizationRatio;
    private String lightDecay;
    private Long ids;
    private Date time;

    public OnuOlt(String id, Double upBandwidth, Double downBandwidth, Double upRate, Double downRate, Double upUtilizationRatio, Double downUtilizationRatio, String lightDecay, Long ids, Date time) {
        this.id = id;
        this.upBandwidth = upBandwidth;
        this.downBandwidth = downBandwidth;
        this.upRate = upRate;
        this.downRate = downRate;
        this.upUtilizationRatio = upUtilizationRatio;
        this.downUtilizationRatio = downUtilizationRatio;
        this.lightDecay = lightDecay;
        this.ids = ids;
        this.time = time;
    }

    @Override
    public String toString() {
        return "OnuOlt{" +
                "id='" + id + '\'' +
                ", upBandwidth=" + upBandwidth +
                ", downBandwidth=" + downBandwidth +
                ", upRate=" + upRate +
                ", downRate=" + downRate +
                ", upUtilizationRatio=" + upUtilizationRatio +
                ", downUtilizationRatio=" + downUtilizationRatio +
                ", lightDecay='" + lightDecay + '\'' +
                ", ids=" + ids +
                ", time=" + time +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getUpBandwidth() {
        return upBandwidth;
    }

    public void setUpBandwidth(Double upBandwidth) {
        this.upBandwidth = upBandwidth;
    }

    public Double getDownBandwidth() {
        return downBandwidth;
    }

    public void setDownBandwidth(Double downBandwidth) {
        this.downBandwidth = downBandwidth;
    }

    public Double getUpRate() {
        return upRate;
    }

    public void setUpRate(Double upRate) {
        this.upRate = upRate;
    }

    public Double getDownRate() {
        return downRate;
    }

    public void setDownRate(Double downRate) {
        this.downRate = downRate;
    }

    public Double getUpUtilizationRatio() {
        return upUtilizationRatio;
    }

    public void setUpUtilizationRatio(Double upUtilizationRatio) {
        this.upUtilizationRatio = upUtilizationRatio;
    }

    public Double getDownUtilizationRatio() {
        return downUtilizationRatio;
    }

    public void setDownUtilizationRatio(Double downUtilizationRatio) {
        this.downUtilizationRatio = downUtilizationRatio;
    }

    public String getLightDecay() {
        return lightDecay;
    }

    public void setLightDecay(String lightDecay) {
        this.lightDecay = lightDecay;
    }

    public Long getIds() {
        return ids;
    }

    public void setIds(Long ids) {
        this.ids = ids;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
