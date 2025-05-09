package org.eiselemillerjammu.leaderelection.driver;

import org.eiselemillerjammu.leaderelection.Simulator;
import org.eiselemillerjammu.leaderelection.raft.RaftNode;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Driver class for the bully algorithm
 */
public class RaftDriver {
    private static final int NUM_NODES = 5;

    public static void main(String[] args) {
        final Simulator simulator = new Simulator();

        final List<RaftNode> nodes = IntStream.range(0, NUM_NODES)
                .mapToObj(i -> new RaftNode(i, simulator))
                .toList();

        for (var node : nodes) {
            node.groupNodes().addAll(nodes);
        }

        nodes.getFirst().startElection();
        simulator.run();

    }
}
