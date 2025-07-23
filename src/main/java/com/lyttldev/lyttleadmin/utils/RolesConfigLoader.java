package com.lyttldev.lyttleadmin.utils;

import com.lyttldev.lyttleadmin.types.*;

import java.util.List;
import java.util.Map;

public class RolesConfigLoader {
    public static RolesConfig fromYamlObject(Object yamlObj) {
        if (!(yamlObj instanceof Map)) return null;
        Map<String, Object> map = (Map<String, Object>) yamlObj;
        RolesConfig rolesConfig = new RolesConfig();

        // Map<String, RoleConfig>
        Map<String, RoleConfig> rolesMap = new java.util.HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String roleName = entry.getKey();
            Object roleObj = entry.getValue();
            if (roleObj instanceof Map) {
                rolesMap.put(roleName, RoleConfigLoader.fromMap((Map<String, Object>) roleObj));
            }
        }
        rolesConfig.setRoles(rolesMap);
        return rolesConfig;
    }
}

// Similarly, implement RoleConfigLoader.fromMap(Map<String, Object> map), etc.
// See below for a basic example:
class RoleConfigLoader {
    public static RoleConfig fromMap(Map<String, Object> map) {
        RoleConfig rc = new RoleConfig();
        rc.setName((String) map.get("name"));
        rc.setPermission((String) map.get("permission"));
        if (map.containsKey("actions")) {
            rc.setActions(ActionsConfigLoader.fromMap((Map<String, Object>) map.get("actions")));
        }
        return rc;
    }
}

class ActionsConfigLoader {
    public static ActionsConfig fromMap(Map<String, Object> map) {
        ActionsConfig ac = new ActionsConfig();
        if (map.containsKey("on_enable"))
            ac.setOn_enable(RoleActionLoader.fromMap((Map<String, Object>) map.get("on_enable")));
        if (map.containsKey("on_disable"))
            ac.setOn_disable(RoleActionLoader.fromMap((Map<String, Object>) map.get("on_disable")));
        if (map.containsKey("gamemode"))
            ac.setGamemode((String) map.get("gamemode"));
        return ac;
    }
}

class RoleActionLoader {
    public static RoleAction fromMap(Map<String, Object> map) {
        RoleAction ra = new RoleAction();
        if (map.containsKey("give"))
            ra.setGive(RoleChangeLoader.fromMap((Map<String, Object>) map.get("give")));
        if (map.containsKey("remove"))
            ra.setRemove(RoleChangeLoader.fromMap((Map<String, Object>) map.get("remove")));
        return ra;
    }
}

class RoleChangeLoader {
    public static RoleChange fromMap(Map<String, Object> map) {
        RoleChange rc = new RoleChange();
        rc.setOperator(Boolean.parseBoolean(String.valueOf(map.get("operator"))));
        rc.setRoles((List<String>) map.get("roles"));
        if (map.containsKey("broadcast"))
            rc.setBroadcast(BroadcastConfigLoader.fromMap((Map<String, Object>) map.get("broadcast")));
        return rc;
    }
}

class BroadcastConfigLoader {
    public static BroadcastConfig fromMap(Map<String, Object> map) {
        BroadcastConfig bc = new BroadcastConfig();
        bc.setGlobal(Boolean.parseBoolean(String.valueOf(map.get("global"))));
        bc.setMessage((String) map.get("message"));
        bc.setPermission((String) map.get("permission"));
        return bc;
    }
}