package org.jammu.leaderelection.bully;

import org.jammu.leaderelection.Message;

public sealed interface BullyMessage extends Message {
    record Election(BullyNode source) implements BullyMessage{}
    record Answer() implements BullyMessage{}
    record Coordinator(BullyNode source) implements BullyMessage{}
}
