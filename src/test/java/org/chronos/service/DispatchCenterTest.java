package org.chronos.service;

import org.chronos.model.PackageStatus;
import org.chronos.model.PackageType;
import org.chronos.model.Package;
import org.chronos.model.Rider;
import org.chronos.model.RiderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DispatchCenterTest {

    private DispatchCenter dispatchCenter;

    @BeforeEach
    public void setup() {
        dispatchCenter = new DispatchCenter();
    }

    @Test
    public void testPackageAssignment() {
        Package pkg = new Package("PKG1", PackageType.EXPRESS, System.currentTimeMillis(), System.currentTimeMillis() + 60000, false);
        Rider rider = new Rider("R1", RiderStatus.AVAILABLE, 5.0, true);

        dispatchCenter.updateRider(rider);
        dispatchCenter.placeOrder(pkg);

        assertEquals(PackageStatus.ASSIGNED, pkg.getStatus());
        assertEquals(RiderStatus.DELIVERING, rider.getStatus());
        assertFalse(dispatchCenter.getAssignments().isEmpty());
    }

    @Test
    public void testSimulateDeliveryReliability() throws InterruptedException {
        long now = System.currentTimeMillis();
        long future = now + 60000;
        Package pkg = new Package("PKG2", PackageType.STANDARD, now, future, false);
        Rider rider = new Rider("R2", RiderStatus.AVAILABLE, 4.0, true);

        dispatchCenter.updateRider(rider);
        dispatchCenter.placeOrder(pkg);
        dispatchCenter.simulateDelivery("PKG2");

        assertEquals(PackageStatus.DELIVERED, pkg.getStatus());
        assertEquals(RiderStatus.AVAILABLE, rider.getStatus());
        assertTrue(rider.getReliability() > 4.0);
    }

    @Test
    public void testRiderGoesOffline() {
        Package pkg = new Package("PKG3", PackageType.EXPRESS, System.currentTimeMillis(), System.currentTimeMillis() + 60000, false);
        Rider rider = new Rider("R3", RiderStatus.AVAILABLE, 5.0, true);

        dispatchCenter.updateRider(rider);
        dispatchCenter.placeOrder(pkg);

        rider.setStatus(RiderStatus.UNAVAILABLE);
        dispatchCenter.updateRider(rider);

        assertEquals(PackageStatus.PENDING, pkg.getStatus());
    }

    @Test
    public void testGetStatus() {
        Package pkg = new Package("PKG4", PackageType.STANDARD, System.currentTimeMillis(), System.currentTimeMillis() + 100000, false);
        Rider rider = new Rider("R4", RiderStatus.AVAILABLE, 5.0, true);

        dispatchCenter.placeOrder(pkg);
        dispatchCenter.updateRider(rider);

        assertNotNull(dispatchCenter.getStatus("PKG4"));
        assertNotNull(dispatchCenter.getStatus("R4"));
    }

    @Test
    public void testMissedExpressDelivery() throws InterruptedException {
        long now = System.currentTimeMillis();
        long past = now - 1000;
        Package pkg = new Package("PKG5", PackageType.EXPRESS, now, past, false);
        Rider rider = new Rider("R5", RiderStatus.AVAILABLE, 5.0, true);

        dispatchCenter.updateRider(rider);
        dispatchCenter.placeOrder(pkg);
        dispatchCenter.simulateDelivery("PKG5");

        List<Package> missed = dispatchCenter.getMissedExpressDeliveries();
        assertTrue(missed.contains(pkg));
    }
}