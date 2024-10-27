package test;

import bean.HttpResponse;

/**
 * test client sending http request to server
 */
public class TestClient {

    public static void main(String[] args) {
        // argument 1: port  argument 2: http request
        if (args.length < 2) {
            System.out.println("Usage: java TestClient <server port> <http request>");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);

        String request = args[1];
        System.out.println("Sending request to http://localhost:" + port + " \n" + request);

        HttpResponse response = FunctionTest.sendRequest(port, request.getBytes());
        System.out.println("Response: \n" + response);
    }
}
