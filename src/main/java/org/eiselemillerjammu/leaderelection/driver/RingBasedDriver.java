package org.eiselemillerjammu.leaderelection.driver;

import org.eiselemillerjammu.leaderelection.Simulator;
import org.eiselemillerjammu.leaderelection.ringbased.RingNode;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Driver class for the ring-based algorithm
 */
public final class RingBasedDriver {

    private static final int NUM_NODES = 5;

    public static void main(String[] args) {

        final Simulator simulator = new Simulator();

        final List<RingNode> nodes = IntStream.range(0, NUM_NODES)
                .mapToObj(i -> new RingNode(i, simulator))
                .toList();

        assignNeighborNodes(nodes);


        nodes.getFirst().startElection();
        simulator.run();

    }

    private static void assignNeighborNodes(List<RingNode> nodes) {
        for (var node : nodes) {
            int index = (node.id() + 1) % NUM_NODES;
            node.setNextNode(nodes.get(index));
        }
    }
}
