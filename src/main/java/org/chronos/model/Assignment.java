package org.chronos.model;

public class Assignment {

    private String packageId;
    private String riderId;
    private long pickupTime;
    private long deliveryTime;

    public Assignment(String packageId, String riderId, long pickupTime, long deliveryTime) {
        this.packageId = packageId;
        this.riderId = riderId;
        this.pickupTime = pickupTime;
        this.deliveryTime = deliveryTime;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getRiderId() {
        return riderId;
    }

    public void setRiderId(String riderId) {
        this.riderId = riderId;
    }

    public long getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(long pickupTime) {
        this.pickupTime = pickupTime;
    }

    public long getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(long deliveryTime) {
        this.deliveryTime = deliveryTime;
    }
}
