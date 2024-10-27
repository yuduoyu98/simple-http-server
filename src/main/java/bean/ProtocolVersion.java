package bean;

import exception.MalformedException;

/**
 * Protocol Version
 */
public class ProtocolVersion {

    private final String protocol;
    private final int major;
    private final int minor;

    public static final ProtocolVersion HTTP_1_1 = new ProtocolVersion("HTTP", 1, 1);

    public ProtocolVersion(String protocol, int major, int minor) {
        this.protocol = protocol;
        this.major = major;
        this.minor = minor;
    }

    public static ProtocolVersion parse(String protocolVersion) throws MalformedException {
        // format check
        if (protocolVersion == null || !protocolVersion.matches("^HTTP/(\\d+)(\\.\\d+)?$")) {
            throw new MalformedException("Invalid protocol version: " + protocolVersion);
        }

        String[] parts = protocolVersion.split("/");
        String protocol = parts[0];
        parts = parts[1].split("\\.");
        int major = Integer.parseInt(parts[0]);
        int minor = parts.length == 2 ? Integer.parseInt(parts[1]) : 0;
        return new ProtocolVersion(protocol, major, minor);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(protocol).append("/").append(major);
        if (minor != 0) sb.append(".").append(minor);
        return sb.toString();
    }

}
