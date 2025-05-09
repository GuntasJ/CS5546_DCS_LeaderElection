package org.eiselemillerjammu.leaderelection.bully;

import org.eiselemillerjammu.leaderelection.AbstractNode;
import org.eiselemillerjammu.leaderelection.Event;
import org.eiselemillerjammu.leaderelection.Simulator;

import java.time.Duration;

/**
 * A class that represents a bully node in the bully algorithm
 */
public final class BullyNode extends AbstractNode<BullyMessage> {

    /**
     * The duration to which a node will wait before hearing an answer
     */
    private static final Duration TIMEOUT = Duration.ofMillis(1_000);

    /**
     * True if the node is waiting for an answer, false otherwise
     */
    private boolean isWaiting;

    public BullyNode(int id, Simulator simulator) {
        super(id, simulator);
        isWaiting = false;
    }

    /**
     * Adds an event indicating that this node is waiting for an answer. If this node is still waiting by the time
     * that the event is called, this node will then start its own election
     */
    private void waitForAnswer() {
        isWaiting = true;
        simulator.addEvent(new Event(simulator.getCurrentTime().plus(TIMEOUT), () -> {
            if (isWaiting) {
                simulator.clearEvents();
//                System.out.println("[DEBUG]: Coordinator is " + this);
//                System.out.println("[DEBUG]: Time is " + simulator.getCurrentTime().toMillis() + "ms");
                groupNodes.forEach(node -> sendMessage(node, new BullyMessage.Coordinator(this)));
            }
        }));
    }

    @Override
    public void startElection() {
        if (!isAlive) {
            return;
        }

        BullyMessage message = new BullyMessage.Election(this);
        groupNodes.stream()
                .filter(node -> node.id() > id)
                .forEach(destinationNode -> sendMessage(destinationNode, message));

        waitForAnswer();
    }


    @Override
    public void receiveMessage(BullyMessage message) {
        if (!isAlive) {
            return;
        }
        switch (message) {
            case BullyMessage.Election(var source) -> {
                sendMessage(source, new BullyMessage.Answer());
                startElection();
            }
            case BullyMessage.Answer() -> isWaiting = false;
            case BullyMessage.Coordinator(var source) -> coordinator = source;
        }
    }
}
