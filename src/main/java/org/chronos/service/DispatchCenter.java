package org.chronos.service;

import org.chronos.model.Assignment;
import org.chronos.model.Package;
import org.chronos.model.PackageStatus;
import org.chronos.model.Rider;
import org.chronos.model.RiderStatus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class DispatchCenter {

    private final Map<String, Package> packages = new HashMap<>();
    private final Map<String, Rider> riders = new HashMap<>();
    private final AuditService auditLogger = new AuditService(packages);

    private final PriorityQueue<Package> pendingPackages = new PriorityQueue<>(
            Comparator.comparing(Package::getType).reversed()
                    .thenComparing(Package::getDeliveryDeadline)
                    .thenComparing(Package::getOrderTimestamp)
    );

    public void placeOrder(Package pkg) {
        packages.put(pkg.getId(), pkg);
        pendingPackages.offer(pkg);
        auditLogger.log("Package placed: " + pkg.getId());
        assignPackages();
    }

    public void updateRider(Rider rider) {
        // If new rider, set default reliability
        if (!riders.containsKey(rider.getId())) {
            rider.setReliability(5.0);
        }

        // Check if going offline mid-delivery
        if (rider.getStatus() == RiderStatus.UNAVAILABLE) {
            auditLogger.getAllAssignments().stream()
                    .filter(a -> a.getRiderId().equals(rider.getId()) && a.getDeliveryTime() == 0)
                    .forEach(a -> {
                        Package pkg = packages.get(a.getPackageId());
                        if (pkg != null && pkg.getStatus() == PackageStatus.ASSIGNED) {
                            pkg.setStatus(PackageStatus.PENDING);
                            pendingPackages.offer(pkg);
                            auditLogger.log("Rider " + rider.getId() + " went offline. Package " + pkg.getId() + " reassigned to queue.");
                        }
                    });
        }

        riders.put(rider.getId(), rider);
        auditLogger.log("Rider updated: " + rider.getId() + " Status: " + rider.getStatus());
        assignPackages();
    }

    public void assignPackages() {
        List<Package> toBeRemoved = new ArrayList<>();

        for (Package pkg : pendingPackages) {
            Optional<Rider> matchedRider = riders.values().stream()
                    .filter(r -> r.getStatus() == RiderStatus.AVAILABLE)
                    .filter(r -> !pkg.isFragile() || r.isCanHandleFragile())
                    .findFirst();

            if (matchedRider.isPresent()) {
                Rider rider = matchedRider.get();
                long now = System.currentTimeMillis();

                pkg.setStatus(PackageStatus.ASSIGNED);
                rider.setStatus(RiderStatus.DELIVERING);

                Assignment assignment = new Assignment(pkg.getId(), rider.getId(), now, 0);
                auditLogger.logAssignment(assignment);
                auditLogger.log("Package assigned: " + pkg.getId() + " to Rider: " + rider.getId());

                toBeRemoved.add(pkg);
            }
        }

        pendingPackages.removeAll(toBeRemoved);
    }

    public void simulateDelivery(String packageId) {
        Package pkg = packages.get(packageId);
        if (pkg == null || pkg.getStatus() != PackageStatus.ASSIGNED) return;

        pkg.setStatus(PackageStatus.DELIVERED);
        Assignment assignment = auditLogger.getAllAssignments().stream()
                .filter(a -> a.getPackageId().equals(packageId))
                .findFirst()
                .orElse(null);

        if (assignment != null) {
            long deliveryTime = System.currentTimeMillis();
            assignment.setDeliveryTime(deliveryTime);

            Rider rider = riders.get(assignment.getRiderId());
            if (rider != null) {
                // Reliability adjustment
                if (deliveryTime > pkg.getDeliveryDeadline()) {
                    rider.setReliability(Math.max(0, rider.getReliability() - 1));
                } else {
                    rider.setReliability(Math.min(5.0, rider.getReliability() + 0.1));
                }
                rider.setStatus(RiderStatus.AVAILABLE);
            }

            auditLogger.log("Package delivered: " + packageId + " by Rider: " + assignment.getRiderId());
        }
    }

    public Object getStatus(String id) {
        if (packages.containsKey(id)) return packages.get(id);
        if (riders.containsKey(id)) return riders.get(id);
        return auditLogger.getAllAssignments().stream()
                .filter(a -> a.getPackageId().equals(id) || a.getRiderId().equals(id))
                .collect(Collectors.toList());
    }

    public List<Assignment> getAssignments() {
        return auditLogger.getAllAssignments();
    }

    public List<Package> getAllPackages() {
        return new ArrayList<>(packages.values());
    }

    public List<Rider> getAllRiders() {
        return new ArrayList<>(riders.values());
    }

    public List<String> getAuditLogs() {
        return auditLogger.getAllLogs();
    }

    public List<String> getRecentAuditLogs(long withinMillis) {
        return auditLogger.getLogsInLastNMillis(withinMillis);
    }

    public List<String> getLogsByKeyword(String keyword) {
        return auditLogger.getLogsByKeyword(keyword);
    }

    public List<Package> getDeliveredByRiderInLast24Hours(String riderId) {
        return auditLogger.getDeliveredByRiderInLast24Hours(riderId);
    }

    public List<Package> getMissedExpressDeliveries() {
        return auditLogger.getMissedExpressDeliveries();
    }
}