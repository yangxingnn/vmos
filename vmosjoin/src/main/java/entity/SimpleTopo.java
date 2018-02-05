package entity;

import java.io.Serializable;

/**
 * @author: yangx
 * @date: 2018/1/22
 * @description:
 */
public class SimpleTopo implements Serializable{
    private String customerId;
    private String oltDownId;
    private String oltUpId;
    private String linkId;

    public SimpleTopo(String customerId, String oltDownId, String oltUpId, String linkId) {
        this.customerId = customerId;
        this.oltDownId = oltDownId;
        this.oltUpId = oltUpId;
        this.linkId = linkId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getOltDownId() {
        return oltDownId;
    }

    public void setOltDownId(String oltDownId) {
        this.oltDownId = oltDownId;
    }

    public String getOltUpId() {
        return oltUpId;
    }

    public void setOltUpId(String oltUpId) {
        this.oltUpId = oltUpId;
    }

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    @Override
    public String toString() {
        return customerId + "," + oltDownId + "," + oltUpId +"," + linkId ;
    }
}
