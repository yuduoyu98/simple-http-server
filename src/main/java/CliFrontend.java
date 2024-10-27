import org.apache.commons.cli.*;
import utils.Logger;

import java.io.IOException;

/**
 * client command line interface
 */
public class CliFrontend {

    public static void main(String[] args) {

        // default values
        int port = 8080;
        int threads = 3;

        // options
        Options options = new Options();
        options.addOption("p", "port", true, "Set the server port");
        options.addOption("t", "threads", true, "Set the number of threads in the thread pool");

        // parser
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;

        try {
            // parse the command line arguments
            cmd = parser.parse(options, args);

            if (cmd.hasOption("p")) {
                port = Integer.parseInt(cmd.getOptionValue("p"));
            }

            if (cmd.hasOption("t")) {
                threads = Integer.parseInt(cmd.getOptionValue("t"));
            }

        } catch (ParseException e) {
            Logger.error("Parsing failed. Reason: " + e.getMessage());
            printHelp(options);
            return;
        } catch (NumberFormatException e) {
            Logger.error("Invalid number format for port or threads. Please use integer values.");
            printHelp(options);
            return;
        }

        Logger.setLogLevel(Logger.LogLevel.INFO);

        // start HttpServer
        HttpServer server = new HttpServer(port, threads);
        try {
            server.start();
        } catch (IOException e) {
            Logger.error("Server could not start: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalStateException e) {
            Logger.error(e.getMessage());
        }
    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("HttpServerCliFrontEnd", options);
    }
}
