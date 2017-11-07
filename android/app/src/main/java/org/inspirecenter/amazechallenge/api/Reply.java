package org.inspirecenter.amazechallenge.api;

import java.util.Arrays;

/**
 * @author Nearchos
 *         Created: 06-Nov-17
 */

public class Reply {
    protected String status;
    protected String [] messages;

    public String getStatus() {
        return status;
    }

    public String[] getMessages() {
        return messages;
    }

    public boolean isOk() {
        return "ok".equalsIgnoreCase(status);
    }

    @Override
    public String toString() {
        return "Reply{" +
                "status='" + status + '\'' +
                ", messages=" + Arrays.toString(messages) +
                '}';
    }
}