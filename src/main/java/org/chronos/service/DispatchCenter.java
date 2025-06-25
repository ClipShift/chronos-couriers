package org.chronos.service;

import org.chronos.model.Assignment;
import org.chronos.model.Package;
import org.chronos.model.PackageStatus;
import org.chronos.model.Rider;
import org.chronos.model.RiderStatus;
import org.chronos.model.Package;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class DispatchCenter {

    private final Map<String, Package> packages = new HashMap<>();
    private final Map<String, Rider> riders = new HashMap<>();
    private final List<Assignment> assignments = new ArrayList<>();

    private final PriorityQueue<Package> pendingPackages = new PriorityQueue<>(
            Comparator.comparing(Package::getType).reversed()
                    .thenComparing(Package::getDeliveryDeadline)
                    .thenComparing(Package::getOrderTimestamp)
    );

    public void placeOrder(Package pkg) {
        packages.put(pkg.getId(), pkg);
        pendingPackages.offer(pkg);
        assignPackages();
    }

    public void updateRider(Rider rider) {
        riders.put(rider.getId(), rider);
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
                assignments.add(new Assignment(pkg.getId(), rider.getId(), now, 0));
                toBeRemoved.add(pkg);
            }
        }

        pendingPackages.removeAll(toBeRemoved);
    }

    public void simulateDelivery(String packageId) {
        Package pkg = packages.get(packageId);
        if (pkg == null || pkg.getStatus() != PackageStatus.ASSIGNED) return;

        pkg.setStatus(PackageStatus.DELIVERED);
        Assignment assignment = assignments.stream()
                .filter(a -> a.getPackageId().equals(packageId))
                .findFirst()
                .orElse(null);
        if (assignment != null) {
            assignment.setDeliveryTime(System.currentTimeMillis());
            Rider rider = riders.get(assignment.getRiderId());
            if (rider != null) rider.setStatus(RiderStatus.AVAILABLE);
        }
    }

    public Object getStatus(String id) {
        if (packages.containsKey(id)) return packages.get(id);
        if (riders.containsKey(id)) return riders.get(id);
        return assignments.stream()
                .filter(a -> a.getPackageId().equals(id) || a.getRiderId().equals(id))
                .collect(Collectors.toList());
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public List<Package> getAllPackages() {
        return new ArrayList<>(packages.values());
    }

    public List<Rider> getAllRiders() {
        return new ArrayList<>(riders.values());
    }
}
