package thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPoolExecutor {
    private final BlockingQueue<Runnable> taskQueue;
    private final Thread[] threads;
    private volatile boolean running;

    public ThreadPoolExecutor(int numThreads) {
        this.threads = new Thread[numThreads];
        this.taskQueue = new LinkedBlockingQueue<>();
        this.running = true;

        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(() -> {
                try {
                    while (running || !taskQueue.isEmpty()) {
                        Runnable task = taskQueue.take();
                        task.run();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            threads[i].start();
        }
    }

    public void submit(Runnable task) {
        if (!running) {
            throw new IllegalStateException("Thread pool is not running.");
        }
        taskQueue.offer(task);
    }

    public void shutdown() {
        this.running = false;
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }

}