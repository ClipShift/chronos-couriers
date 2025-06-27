package org.chronos.service;

import org.chronos.model.Package;
import org.chronos.model.PackageStatus;
import org.chronos.model.PackageType;
import org.chronos.model.Rider;
import org.chronos.model.RiderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class CLIServiceTest {

    private DispatchCenter dispatchCenter;
    private CLIService cli;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    public void setup() {
        dispatchCenter = new DispatchCenter();
        cli = new CLIService(dispatchCenter);
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void testPlaceOrder() {
        cli.handle("place_order EXPRESS 5 false");
        assertTrue(outContent.toString().contains("Order placed: PKG"));
        assertFalse(dispatchCenter.getAllPackages().isEmpty());
    }

    @Test
    public void testUpdateRiderStatus() {
        cli.handle("update_rider_status 101 AVAILABLE true 5.0");
        assertTrue(outContent.toString().contains("Rider updated: RD101"));
        Rider rider = dispatchCenter.getAllRiders().get(0);
        assertEquals("RD101", rider.getId());
        assertEquals(5.0, rider.getReliability());
    }

    @Test
    public void testSimulateDelivery() {
        cli.handle("update_rider_status 202 AVAILABLE true");
        cli.handle("place_order STANDARD 1 false");
        String pkgId = dispatchCenter.getAllPackages().get(0).getId();
        cli.handle("simulate_delivery " + pkgId);
        assertTrue(outContent.toString().contains("Delivery simulated for package: " + pkgId));
        assertEquals(PackageStatus.DELIVERED, dispatchCenter.getAllPackages().get(0).getStatus());
    }

    @Test
    public void testGetStatus() {
        cli.handle("update_rider_status 303 AVAILABLE false");
        cli.handle("get_status RD303");
        assertTrue(outContent.toString().contains("RD303"));
    }

    @Test
    public void testHelp() {
        cli.handle("help");
        assertTrue(outContent.toString().contains("Available Commands:"));
        assertTrue(outContent.toString().contains("place_order"));
        assertTrue(outContent.toString().contains("update_rider_status"));
    }

    @Test
    public void testUnknown() {
        cli.handle("unknown");
        assertTrue(outContent.toString().contains("Unknown command"));
    }
}
