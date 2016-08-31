package com.vmware.pso.samples.core.model.types;

public enum Status {

    WAITING(false), APPROVED(true), REJECTED(false);

    final private boolean approved;

    private Status(final boolean approved) {
        this.approved = approved;
    }

    public boolean isApproved() {
        return approved;
    }
}
