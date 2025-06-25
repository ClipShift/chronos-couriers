package org.chronos.model;

public class Package {

    private String id;
    private PackageType type;
    private long orderTimestamp;
    private long deliveryDeadline;
    private boolean fragile;
    private PackageStatus status;

    public Package(String id, PackageType type, long orderTimestamp, long deliveryDeadline, boolean fragile) {
        this.id = id;
        this.type = type;
        this.orderTimestamp = orderTimestamp;
        this.deliveryDeadline = deliveryDeadline;
        this.fragile = fragile;
        this.status = PackageStatus.PENDING;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PackageType getType() {
        return type;
    }

    public void setType(PackageType type) {
        this.type = type;
    }

    public long getOrderTimestamp() {
        return orderTimestamp;
    }

    public void setOrderTimestamp(long orderTimestamp) {
        this.orderTimestamp = orderTimestamp;
    }

    public long getDeliveryDeadline() {
        return deliveryDeadline;
    }

    public void setDeliveryDeadline(long deliveryDeadline) {
        this.deliveryDeadline = deliveryDeadline;
    }

    public boolean isFragile() {
        return fragile;
    }

    public void setFragile(boolean fragile) {
        this.fragile = fragile;
    }

    public PackageStatus getStatus() {
        return status;
    }

    public void setStatus(PackageStatus status) {
        this.status = status;
    }
}
