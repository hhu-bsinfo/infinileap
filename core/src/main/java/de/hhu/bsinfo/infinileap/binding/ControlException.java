package de.hhu.bsinfo.infinileap.binding;

/**
 * Checked exception thrown when a native control path operation fails.
 * Check {@link #status()} for more information.
 */
public final class ControlException extends Exception {

    private final Status status;

    ControlException(int status) {
        this(Status.of(status));
    }

    ControlException(Status status) {
        super(status.message());
        this.status = status;
    }

    public final Status status() {
        return status;
    }
}
