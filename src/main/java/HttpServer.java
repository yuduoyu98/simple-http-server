import thread.ThreadPoolExecutor;
import utils.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Http Server
 */
public class HttpServer {

    private ServerSocket serverSocket;
    private final ThreadPoolExecutor threadPoolExecutor;
    private final int port;
    private volatile boolean running;

    public HttpServer(int port, int numThreads) {
        this.port = port;
        this.threadPoolExecutor = new ThreadPoolExecutor(numThreads);
        this.running = false;
    }

    public void start() throws IOException {
        if (running) {
            throw new IllegalStateException("Server already started.");
        }

        serverSocket = new ServerSocket(port);
        running = true;

        Logger.info("Server is running on port " + port);

        while (running) {
            try {
                Socket socket = serverSocket.accept();
                threadPoolExecutor.submit(new HttpTask(socket));
            } catch (IOException e) {
                Logger.error("Error accepting connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        if (!running) {
            throw new IllegalStateException("Server is not running.");
        }

        running = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            Logger.error("Error closing server socket: " + e.getMessage());
            e.printStackTrace();
        }
        threadPoolExecutor.shutdown();
    }

    public boolean isRunning() {
        return running;
    }
}
