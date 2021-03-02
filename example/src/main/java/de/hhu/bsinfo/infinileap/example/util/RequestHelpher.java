package de.hhu.bsinfo.infinileap.example.util;

import de.hhu.bsinfo.infinileap.binding.Request;
import de.hhu.bsinfo.infinileap.binding.Worker;
import de.hhu.bsinfo.infinileap.binding.WorkerProgress;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class RequestHelpher {

    public static void poll(Worker worker, AtomicBoolean value) {
        while (!value.get()) {
            worker.progress();
        }

        value.set(false);
    }

    public static void await(Worker worker, AtomicBoolean value) {
        while (!value.get()) {
            if (worker.progress() == WorkerProgress.IDLE) {
                worker.await();
            };
        }

        value.set(false);
    }

    public static void poll(Worker worker, CommunicationBarrier barrier) {
        while (!barrier.isReleased()) {
            worker.progress();
        }

        barrier.reset();
    }

    public static void await(Worker worker, CommunicationBarrier barrier) {
        while (!barrier.isReleased()) {
            if (worker.progress() == WorkerProgress.IDLE) {
                worker.await();
            };
        }

        barrier.reset();
    }

    public static void poll(Worker worker, AtomicReference<?> value) {
        while (value.get() == null) {
            worker.progress();
        }
    }

    public static void await(Worker worker, AtomicReference<?> value) {
        while (value.get() == null) {
            if (worker.progress() == WorkerProgress.IDLE) {
                worker.await();
            };
        }
    }

    public static void poll(Worker worker, Request request) {
        while (request.state() != Request.State.COMPLETE) {
            worker.progress();
        }

        request.release();
    }

    public static void await(Worker worker, Request request) {
        while (request.state() != Request.State.COMPLETE) {
            if (worker.progress() == WorkerProgress.IDLE) {
                worker.await();
            }
        }

        request.release();
    }
}
