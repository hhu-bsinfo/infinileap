package de.hhu.bsinfo.infinileap.util;

import de.hhu.bsinfo.infinileap.binding.Status;
import de.hhu.bsinfo.infinileap.binding.Worker;

public class RequestPool {
    
    private final long[] requests;

    private int consumerIndex;
    private int producerIndex;


    public RequestPool(int size) {
        requests = new long[size];
    }

    public final boolean add(long request) {
        if (Status.is(request, Status.OK)) {
            return false;
        }

        requests[producerIndex++] = request;
        return true;
    }

    public final void pollRemaining(Worker worker) {
        for (int i = consumerIndex; i < producerIndex; i++) {
            Requests.poll(worker, requests[i]);
        }

        rewind();
    }

    public int count() {
        return producerIndex - consumerIndex;
    }

    public final void poll(Worker worker) {
        Requests.poll(worker, requests[consumerIndex++]);
    }

    public final void rewind() {
        producerIndex = 0;
        consumerIndex = 0;
    }
}
