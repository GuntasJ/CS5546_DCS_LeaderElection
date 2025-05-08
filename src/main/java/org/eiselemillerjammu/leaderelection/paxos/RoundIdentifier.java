package org.eiselemillerjammu.leaderelection.paxos;

/**
 * A record that represents the current round identifier
 * @param greatestSoFar The greatest round identifier seen so far
 * @param nodeId The id of the node which is sending this to ensure this round identifier is unique
 */
public record RoundIdentifier(int greatestSoFar, int nodeId) implements Comparable<RoundIdentifier> {

    @Override
    public int compareTo(RoundIdentifier o) {
        if (greatestSoFar != o.greatestSoFar) {
            return Integer.compare(greatestSoFar, o.greatestSoFar);
        }
        return Integer.compare(nodeId, o.nodeId);
    }
}
