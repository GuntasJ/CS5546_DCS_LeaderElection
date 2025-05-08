package org.eiselemillerjammu;

import org.eiselemillerjammu.leaderelection.Simulator;
import org.eiselemillerjammu.leaderelection.raft.RaftNode;

import java.util.List;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        Simulator simulator = new Simulator();
        List<RaftNode> nodes = IntStream.range(0, 5)
                .mapToObj(i -> new RaftNode(i, simulator))
                .toList();

        nodes.forEach(node -> nodes
                .forEach(n -> node.groupNodes().add(n))
        );

        nodes.getFirst().startElection();

        simulator.run();
    }
}