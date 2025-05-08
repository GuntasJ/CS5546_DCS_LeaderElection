package org.jammu.leaderelection.bully;

import org.jammu.leaderelection.AbstractNode;
import org.jammu.leaderelection.Event;
import org.jammu.leaderelection.Simulator;

import java.time.Duration;

public final class BullyNode extends AbstractNode<BullyMessage> {
    private static final Duration TIMEOUT = Duration.ofMillis(1_000);
    private boolean waiting;

    public BullyNode(int id, Simulator simulator) {
        super(id, simulator);
        waiting = false;
    }

    private void waitForAnswer() {
        waiting = true;
        simulator.addEvent(new Event(simulator.getCurrentTime().plus(TIMEOUT), () -> {
            if (waiting) {
                simulator.clearEvents();
                System.out.println("[DEBUG]: Coordinator is " + this);
                System.out.println("[DEBUG]: Time is " + simulator.getCurrentTime().toMillis() + "ms");
                groupNodes.forEach(node -> sendMessage(node, new BullyMessage.Coordinator(this)));
            }
        }));
    }

    @Override
    public void startElection() {
        if (isAlive) {
            BullyMessage message = new BullyMessage.Election(this);
            groupNodes.stream()
                    .filter(node -> node.id() > id)
                    .forEach(destinationNode -> sendMessage(destinationNode, message));

            waitForAnswer();
        }
    }


    @Override
    public void receiveMessage(BullyMessage message) {
        if (isAlive) {
            switch (message) {
                case BullyMessage.Election(var source) -> {
                    sendMessage(source, new BullyMessage.Answer());
                    startElection();
                }
                case BullyMessage.Answer() -> waiting = false;
                case BullyMessage.Coordinator(var source) -> coordinator = source;
            }
        }
    }
}
