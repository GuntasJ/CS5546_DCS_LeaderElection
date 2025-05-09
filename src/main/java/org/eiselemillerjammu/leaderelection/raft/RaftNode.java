package org.eiselemillerjammu.leaderelection.raft;

import org.eiselemillerjammu.leaderelection.AbstractNode;
import org.eiselemillerjammu.leaderelection.Event;
import org.eiselemillerjammu.leaderelection.Simulator;

import java.time.Duration;

/**
 * A class that represents a Raft node in the raft leader election
 */
public final class RaftNode extends AbstractNode<RaftMessage> {
    /**
     * How long a node waits for a heart beat
     */
    private final static Duration HEARTBEAT_WAIT = Duration.ofMillis(1_000);

    /**
     * The current term for the election
     */
    private int term;

    /**
     * How many votes this node has acquired from the group
     */
    private int votes;

    /**
     * If this node has won the election
     */
    private boolean wonElection;

    /**
     * If this node has received a heartbeat
     */
    private boolean receivedHeartbeat;

    /**
     * Initializes base values
     * @param id The unique id of this node
     * @param simulator The simulator reference of this node
     */
    public RaftNode(int id, Simulator simulator) {
        super(id, simulator);
        term = 0;
        votes = 0;
        wonElection = false;
        receivedHeartbeat = false;
    }

    public void reset() {
        term = 0;
        votes = 0;
        wonElection = false;
        receivedHeartbeat = false;
    }

    /**
     * Waits for a future heartbeat
     * If it does not receive a heartbeat, it starts an election.
     * This function will call itself again if it receives a heartbeat, requiring continuous, future heartbeats.
     */
    private void waitForHeartbeats() {
        simulator.addEvent(new Event(simulator.getCurrentTime().plus(HEARTBEAT_WAIT), () -> {
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

    /**
     * Sends heartbeats to other nodes
     * Once this event is called, it will then call this function again,
     * thus sending continuous, future heartbeats.
     */
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
        if (!isAlive) {
            return;
        }
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
