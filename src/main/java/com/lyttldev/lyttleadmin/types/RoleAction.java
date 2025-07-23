package com.lyttldev.lyttleadmin.types;

/**
 * Represents the actions taken when enabling/disabling a mode.
 */
public class RoleAction {
    private RoleChange give;
    private RoleChange remove;

    public RoleChange getGive() {
        return give;
    }

    public RoleChange getRemove() {
        return remove;
    }

    public void setGive(RoleChange give) {
        this.give = give;
    }

    public void setRemove(RoleChange remove) {
        this.remove = remove;
    }
}
