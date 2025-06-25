package org.chronos.model;

public class Rider {
    private String id;
    private RiderStatus status;
    private double reliability;
    private boolean canHandleFragile;

    public Rider(String id, RiderStatus status, double reliability, boolean canHandleFragile) {
        this.id = id;
        this.status = status;
        this.reliability = reliability;
        this.canHandleFragile = canHandleFragile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RiderStatus getStatus() {
        return status;
    }

    public void setStatus(RiderStatus status) {
        this.status = status;
    }

    public double getReliability() {
        return reliability;
    }

    public void setReliability(double reliability) {
        this.reliability = reliability;
    }

    public boolean isCanHandleFragile() {
        return canHandleFragile;
    }

    public void setCanHandleFragile(boolean canHandleFragile) {
        this.canHandleFragile = canHandleFragile;
    }
}
