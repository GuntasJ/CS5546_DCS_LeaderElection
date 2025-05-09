package org.eiselemillerjammu.leaderelection.driver;

import org.eiselemillerjammu.leaderelection.Simulator;
import org.eiselemillerjammu.leaderelection.bully.BullyNode;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Driver class for the bully algorithm
 */
public final class BullyDriver {
    private static final int NUM_NODES = 5;

    public static void main(String[] args) {
        final Simulator simulator = new Simulator();

        List<BullyNode> nodes = IntStream.range(0, NUM_NODES)
                .mapToObj(i -> new BullyNode(i, simulator))
                .toList();

        assignGroupNodes(nodes);

        nodes.getLast().fail();
        nodes.getFirst().startElection();
        simulator.run();
    }

    private static void assignGroupNodes(List<BullyNode> nodes) {
        for (var node : nodes) {
            node.groupNodes().addAll(nodes);
        }
    }
}
