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

        for (int i = 0; i < 1_000; i++) {
            nodes.getFirst().startElection();
            simulator.run();
        }

        Duration time = simulator.getCurrentTime();
        long start = Instant.now().getNano();
        nodes.getFirst().startElection();
        simulator.run();
        long end = Instant.now().getNano();

        System.out.println("Benchmark: Took " + (end - start) + " ns");
        System.out.println("Benchmark: Took " + (simulator.getCurrentTime().minus(time).toMillis()) + " ms (simulation)");
    }

    private static void assignNeighborNodes(List<RingNode> nodes) {
        for (var node : nodes) {
            int index = (node.id() + 1) % NUM_NODES;
            node.setNextNode(nodes.get(index));
        }
    }
}
