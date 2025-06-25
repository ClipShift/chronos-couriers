package org.chronos.model;

public class Rider {
    private String id;
    private RiderStatus status;
    private double reliabilityRating;
    private boolean canHandleFragile;

    public Rider(String id, RiderStatus status, double reliabilityRating, boolean canHandleFragile) {
        this.id = id;
        this.status = status;
        this.reliabilityRating = reliabilityRating;
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

    public double getReliabilityRating() {
        return reliabilityRating;
    }

    public void setReliabilityRating(double reliabilityRating) {
        this.reliabilityRating = reliabilityRating;
    }

    public boolean isCanHandleFragile() {
        return canHandleFragile;
    }

    public void setCanHandleFragile(boolean canHandleFragile) {
        this.canHandleFragile = canHandleFragile;
    }
}
