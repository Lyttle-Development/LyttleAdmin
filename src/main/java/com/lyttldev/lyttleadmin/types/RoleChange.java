package com.lyttldev.lyttleadmin.types;

import java.util.List; /**
 * Represents what gets changed (given or removed) during a role action.
 */
public class RoleChange {
    private boolean operator;
    private List<String> roles;

    public boolean isOperator() {
        return operator;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setOperator(boolean operator) {
        this.operator = operator;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
