package org.eiselemillerjammu.leaderelection.driver;

import org.eiselemillerjammu.leaderelection.Simulator;
import org.eiselemillerjammu.leaderelection.paxos.PaxosNode;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Driver class for the bully algorithm
 */
public class PaxosDriver {
    private static final int NUM_NODES = 15;

    public static void main(String[] args) {
        final Simulator simulator = new Simulator();

        final List<PaxosNode> nodes = IntStream.range(0, NUM_NODES)
                .mapToObj(i -> new PaxosNode(i, simulator))
                .toList();

        for (var node : nodes) {
            node.groupNodes().addAll(nodes);
        }

        for (int i = 0; i < 1000; i++) {
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
}
