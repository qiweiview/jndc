package jndc.core.message;

import java.time.LocalDateTime;

public class BeatMessage extends SerializableMessage {

    private static final long serialVersionUID = 249975684791775075L;

    private LocalDateTime localDateTime;

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    @Override
    public String toString() {
        return "BeatMessage{" +
                "localDateTime=" + localDateTime +
                '}';
    }
}
