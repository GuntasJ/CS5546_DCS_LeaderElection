package org.jammu.leaderelection.raft;

import org.jammu.leaderelection.AbstractNode;
import org.jammu.leaderelection.Event;
import org.jammu.leaderelection.Simulator;

import java.time.Duration;

public final class RaftNode extends AbstractNode<RaftMessage> {
    private final static Duration HEART_BEAT_WAIT = Duration.ofMillis(1_000);
    private int term;
    private int votes;
    private boolean wonElection;
    private boolean receivedHeartbeat;

    public RaftNode(int id, Simulator simulator) {
        super(id, simulator);
        term = 0;
        votes = 0;
        wonElection = false;
        receivedHeartbeat = false;
    }

    private void waitForHeartbeats() {
        simulator.addEvent(new Event(simulator.getCurrentTime().plus(HEART_BEAT_WAIT), () -> {
            if (!receivedHeartbeat) {
                System.out.println("[DEBUG] Node " + this + " missed heartbeat. Starting election.");
                startElection();
            }
            else {
                receivedHeartbeat = false;
                waitForHeartbeats();
            }
        }));
    }

    private void sendHeartBeats() {
        simulator.addEvent(new Event(simulator.getCurrentTime(), () -> {
            if (this == coordinator) {
                broadcastOthers(new RaftMessage.Heartbeat());
                sendHeartBeats();
            }
        }));
    }

    @Override
    public void startElection() {
        if (!isAlive) {
            throw new IllegalStateException("Node is not alive");
        }
        term++;
        broadcastOthers(new RaftMessage.RequestVote(term, this));
    }


    @Override
    public void receiveMessage(RaftMessage message) {
        if (isAlive) {
            switch (message) {
                case RaftMessage.NotifyResult(var leader) -> {
                    coordinator = leader;
                    waitForHeartbeats();
                }
                case RaftMessage.RequestVote(var electionTerm, var source) -> {
                    if (term < electionTerm) {
                        term = electionTerm;
                        sendMessage(source, new RaftMessage.Vote());
                    }
                }
                case RaftMessage.Vote() -> {
                    votes++;
                    if (!wonElection && votes >= groupNodes.size() / 2 - 1) {
                        broadcastOthers(new RaftMessage.NotifyResult(this));
                        coordinator = this;
                        System.out.println("[DEBUG]: " + this + " won the election");
                        wonElection = true;
                        //sendHeartBeats();
                    }
                }
                case RaftMessage.Heartbeat() -> receivedHeartbeat = true;
            }
        }
    }
}
