package bean;

import exception.MalformedException;

/**
 * HTTP-Version SP Status-Code SP Reason-Phrase CRLF
 */
public class StatusLine extends StartLine {

    public final HttpStatus status;

    public StatusLine(ProtocolVersion protocolVersion, HttpStatus status) {
        this.status = status;
        this.protocolVersion = protocolVersion;
    }

    public static StatusLine parse(String statusLine) throws MalformedException {
        String[] parts = statusLine.split(" ");
        if (parts.length < 3) throw new MalformedException("Invalid status line: " + statusLine);

        ProtocolVersion protocolVersion = ProtocolVersion.parse(parts[0]);
        String reasonPhrase = statusLine.split(parts[0] + " " + parts[1])[1].trim();
        HttpStatus status = HttpStatus.parse(Integer.parseInt(parts[1]), reasonPhrase);
        return new StatusLine(protocolVersion, status);
    }

    @Override
    public String toString() {
        return String.join(" ", protocolVersion.toString(), String.valueOf(status.code), status.reasonPhrase);
    }
}
