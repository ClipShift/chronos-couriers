package org.chronos.strategy.impl;

import org.chronos.model.Rider;
import org.chronos.model.RiderStatus;
import org.chronos.model.Package;
import org.chronos.strategy.AssignmentStrategy;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

public class ExpressAssignmentStrategy implements AssignmentStrategy {

    @Override
    public Optional<Rider> findRider(Package pkg, Collection<Rider> riders) {
        return riders.stream()
                .filter(r -> r.getStatus() == RiderStatus.AVAILABLE)
                .filter(r -> !pkg.isFragile() || r.isCanHandleFragile())
                .max(Comparator.comparingDouble(Rider::getReliability));
    }
}

