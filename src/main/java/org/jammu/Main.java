package org.jammu;

import org.jammu.leaderelection.Simulator;
import org.jammu.leaderelection.bully.BullyNode;
import org.jammu.leaderelection.ringbased.RingNode;

import java.util.List;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        Simulator simulator = new Simulator();
        List<BullyNode> nodes = IntStream.range(0, 4)
                .mapToObj(i -> new BullyNode(i, simulator))
                .toList();

        nodes.forEach(node -> nodes.stream()
                .filter(n -> node.getId() != n.getId())
                .forEach(n -> node.getGroupNodes().add(n))
        );

        nodes.getLast().fail();
        nodes.getFirst().fail();
        nodes.get(1).startElection();

        simulator.run();
    }
}