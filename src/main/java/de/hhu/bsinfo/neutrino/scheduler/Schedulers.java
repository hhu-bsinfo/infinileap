package de.hhu.bsinfo.neutrino.scheduler;

import de.hhu.bsinfo.neutrino.util.NamedThreadFactory;
import de.hhu.bsinfo.neutrino.util.SingleThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class Schedulers {

    private static final int PROCESSOR_COUNT = Runtime.getRuntime().availableProcessors();

    private static final Scheduler IO_SCHEDULER = new InputOutputScheduler();
    private static final Scheduler COMP_SCHEDULER = new ComputationScheduler();
    private static final Scheduler MAIN_SCHEDULER = new MainScheduler();

    public static void io(final Runnable runnable) {
        IO_SCHEDULER.schedule(runnable);
    }

    public static void computation(final Runnable runnable) {
        COMP_SCHEDULER.schedule(runnable);
    }

    public static void main(final Runnable runnable) {
        MAIN_SCHEDULER.schedule(runnable);
    }

    private static final class InputOutputScheduler implements Scheduler {

        private static final ThreadFactory THREAD_FACTORY = new NamedThreadFactory("io");
        private final ExecutorService executorService = Executors.newFixedThreadPool(PROCESSOR_COUNT / 2, THREAD_FACTORY);

        @Override
        public void schedule(Runnable runnable) {
            executorService.execute(runnable);
        }
    }

    private static final class ComputationScheduler implements Scheduler {

        private static final ThreadFactory THREAD_FACTORY = new NamedThreadFactory("comp");
        private final ExecutorService executorService = Executors.newFixedThreadPool(PROCESSOR_COUNT / 2, THREAD_FACTORY);


        @Override
        public void schedule(Runnable runnable) {
            executorService.execute(runnable);
        }
    }

    private static final class MainScheduler implements Scheduler {

        private static final ThreadFactory THREAD_FACTORY = new SingleThreadFactory("main");
        private final ExecutorService executorService = Executors.newSingleThreadExecutor(THREAD_FACTORY);

        @Override
        public void schedule(Runnable runnable) {
            executorService.execute(runnable);
        }
    }
}
