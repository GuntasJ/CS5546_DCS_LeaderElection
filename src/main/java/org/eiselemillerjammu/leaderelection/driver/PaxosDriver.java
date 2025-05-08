package org.eiselemillerjammu.leaderelection.driver;

import org.eiselemillerjammu.leaderelection.Simulator;
import org.eiselemillerjammu.leaderelection.paxos.PaxosNode;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Driver class for the bully algorithm
 */
public class PaxosDriver {
    private static final int NUM_NODES = 5;

    public static void main(String[] args) {
        final Simulator simulator = new Simulator();

        final List<PaxosNode> nodes = IntStream.range(0, NUM_NODES)
                .mapToObj(i -> new PaxosNode(i, simulator))
                .toList();

        for (var node : nodes) {
            node.groupNodes().addAll(nodes);
        }

        nodes.getFirst().startElection();

        simulator.run();
    }
}
