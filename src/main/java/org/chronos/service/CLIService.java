package org.chronos.service;

import org.chronos.model.PackageType;
import org.chronos.model.Rider;
import org.chronos.model.RiderStatus;
import org.chronos.model.Package;

public class CLIService {
    private final DispatchCenter dispatchCenter;

    public CLIService(DispatchCenter dispatchCenter) {
        this.dispatchCenter = dispatchCenter;
    }

    public void handle(String input) {
        String[] parts = input.trim().split("\\s+");
        if (parts.length == 0) return;

        String command = parts[0];
        switch (command.toLowerCase()) {
            case "place_order":
                handlePlaceOrder(parts);
                break;
            case "update_rider_status":
                handleUpdateRiderStatus(parts);
                break;
            case "simulate_delivery":
                handleSimulateDelivery(parts);
                break;
            case "get_status":
                handleGetStatus(parts);
                break;
            default:
                System.out.println("Unknown command: " + command);
        }
    }

    private void handlePlaceOrder(String[] parts) {
        if (parts.length < 5) {
            System.out.println("Usage: place_order <package_id> <EXPRESS|STANDARD> <deadline_timestamp> <fragile>");
            return;
        }
        String id = parts[1];
        PackageType type = PackageType.valueOf(parts[2].toUpperCase());
        long deadline = Long.parseLong(parts[3]);
        boolean fragile = Boolean.parseBoolean(parts[4]);
        long now = System.currentTimeMillis();

        Package pkg = new Package(id, type, now, deadline, fragile);
        dispatchCenter.placeOrder(pkg);
        System.out.println("Order placed: " + id);
    }

    private void handleUpdateRiderStatus(String[] parts) {
        if (parts.length < 4) {
            System.out.println("Usage: update_rider_status <rider_id> <AVAILABLE|UNAVAILABLE|DELIVERING> <can_handle_fragile> <reliability_rating>");
            return;
        }
        String id = parts[1];
        RiderStatus status = RiderStatus.valueOf(parts[2].toUpperCase());
        boolean canHandleFragile = Boolean.parseBoolean(parts[3]);
        double rating = parts.length > 4 ? Double.parseDouble(parts[4]) : 5.0;

        Rider rider = new Rider(id, status, rating, canHandleFragile);
        dispatchCenter.updateRider(rider);
        System.out.println("Rider updated: " + id);
    }

    private void handleSimulateDelivery(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Usage: simulate_delivery <package_id>");
            return;
        }
        String id = parts[1];
        dispatchCenter.simulateDelivery(id);
        System.out.println("Delivery simulated for package: " + id);
    }

    private void handleGetStatus(String[] parts) {
        if (parts.length < 2) {
            System.out.println("Usage: get_status <package_id|rider_id>");
            return;
        }
        String id = parts[1];
        Object status = dispatchCenter.getStatus(id);
        System.out.println(status != null ? status.toString() : "No record found for: " + id);
    }
}

