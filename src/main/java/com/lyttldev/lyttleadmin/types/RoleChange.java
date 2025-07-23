package com.lyttldev.lyttleadmin.types;

import java.util.List; /**
 * Represents what gets changed (given or removed) during a role action.
 */
public class RoleChange {
    private boolean operator;
    private List<String> roles;
    private BroadcastConfig broadcast;

    public boolean isOperator() {
        return operator;
    }

    public List<String> getRoles() {
        return roles;
    }

    public BroadcastConfig getBroadcast() {
        return broadcast;
    }

    public void setOperator(boolean operator) {
        this.operator = operator;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public void setBroadcast(BroadcastConfig broadcast) {
        this.broadcast = broadcast;
    }
}
