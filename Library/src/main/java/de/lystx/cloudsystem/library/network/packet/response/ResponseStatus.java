package de.lystx.cloudsystem.library.network.packet.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * Status of a packet process which can be either positive or negative<br>
 * Similar to the HTTP responses
 */
@AllArgsConstructor
public enum ResponseStatus {

    SUCESS(0x00),
    FAILED(0x40),
    FORBIDDEN(0x42),
    CONFLICT(0x43),
    NOT_FOUND(0x44),
    INTERNAL_ERROR(0x81, true),
    BAD_REQUEST(0x82, true);

    @Getter
    private int id;

    @Getter
    private boolean critically;

    @Getter
    private boolean changed;

    ResponseStatus(int id) {
        this(id, false, false);
    }
    ResponseStatus(int id, boolean critically) {
        this(id, critically, false);
    }

    public static ResponseStatus fromID(int id) {
        return Arrays.stream(values()).filter(responseStatus -> responseStatus.getId() == id).findFirst().orElse(null);
    }



    public void setStatus(ResponseStatus responseStatus) {
        id = responseStatus.getId();
        changed = true;
    }

    /**
     * Gets the name (as lower case)
     *
     * @return The name as string
     */
    public String getName() {
        return name().toLowerCase();
    }

    /**
     * Checks if the status is ok
     *
     * @return The result
     */
    public boolean isOk() {
        return this == SUCESS;
    }

    /**
     * Checks if the status is not ok
     *
     * @return The result
     */
    public boolean isNok() {
        return !isOk();
    }

}
