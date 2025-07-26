package com.lyttldev.lyttleadmin.utils;

import com.lyttldev.lyttleadmin.types.*;
import org.bukkit.configuration.MemorySection;

import java.util.List;
import java.util.Map;

public class RolesConfigLoader {
    public static RolesConfig fromConfig(Object rolesObj) {
        if (!(rolesObj instanceof MemorySection)) {
            System.out.println("[LyttleAdmin] Invalid roles configuration. Please check your config.yml file.");
            return null;
        }

        MemorySection rolesSection = (MemorySection) rolesObj;
        Map<String, Object> rolesMap = rolesSection.getValues(false);

        RolesConfig rolesConfig = new RolesConfig();

        for (Map.Entry<String, Object> entry : rolesMap.entrySet()) {
            String roleName = entry.getKey();
            Object roleSectionObj = entry.getValue();
            Map<String, Object> roleData = toMap(roleSectionObj);
            if (roleData != null) {
                RoleConfig roleConfig = RoleConfigLoader.fromMap(roleData);
                rolesConfig.addRole(roleConfig);
            } else {
                System.out.println("[LyttleAdmin] Role section for '" + roleName + "' is invalid, skipping.");
            }
        }

        return rolesConfig;
    }

    // Utility to convert MemorySection or Map to Map<String, Object>
    public static Map<String, Object> toMap(Object obj) {
        if (obj instanceof MemorySection) {
            return ((MemorySection) obj).getValues(false);
        } else if (obj instanceof Map) {
            return (Map<String, Object>) obj;
        }
        return null;
    }
}

class RoleConfigLoader {
    public static RoleConfig fromMap(Map<String, Object> map) {
        RoleConfig rc = new RoleConfig();
        rc.setName((String) map.get("name"));
        rc.setPermission((String) map.get("permission"));
        Object actionsObj = map.get("actions");
        if (actionsObj != null) {
            Map<String, Object> actionsMap = RolesConfigLoader.toMap(actionsObj);
            if (actionsMap != null) {
                rc.setActions(ActionsConfigLoader.fromMap(actionsMap));
            }
        }
        return rc;
    }
}

class ActionsConfigLoader {
    public static ActionsConfig fromMap(Map<String, Object> map) {
        ActionsConfig ac = new ActionsConfig();
        Object onEnableObj = map.get("on_enable");
        if (onEnableObj != null) {
            Map<String, Object> onEnableMap = RolesConfigLoader.toMap(onEnableObj);
            if (onEnableMap != null) ac.setOnEnable(RoleActionLoader.fromMap(onEnableMap));
        }
        Object onDisableObj = map.get("on_disable");
        if (onDisableObj != null) {
            Map<String, Object> onDisableMap = RolesConfigLoader.toMap(onDisableObj);
            if (onDisableMap != null) ac.setOnDisable(RoleActionLoader.fromMap(onDisableMap));
        }
        return ac;
    }
}

class RoleActionLoader {
    public static RoleAction fromMap(Map<String, Object> map) {
        RoleAction ra = new RoleAction();
        Object giveObj = map.get("give");
        if (giveObj != null) {
            Map<String, Object> giveMap = RolesConfigLoader.toMap(giveObj);
            if (giveMap != null) ra.setGive(RoleChangeLoader.fromMap(giveMap));
        }
        Object removeObj = map.get("remove");
        if (removeObj != null) {
            Map<String, Object> removeMap = RolesConfigLoader.toMap(removeObj);
            if (removeMap != null) ra.setRemove(RoleChangeLoader.fromMap(removeMap));
        }
        if (map.containsKey("gamemode"))
            ra.setGamemode((String) map.get("gamemode"));
        if (map.containsKey("action_bar"))
            ra.setActionBar((String) map.get("action_bar"));
        Object broadcastObj = map.get("broadcast");
        if (broadcastObj != null) {
            Map<String, Object> broadcastMap = RolesConfigLoader.toMap(broadcastObj);
            if (broadcastMap != null) ra.setBroadcast(BroadcastConfigLoader.fromMap(broadcastMap));
        }
        return ra;
    }
}

class RoleChangeLoader {
    public static RoleChange fromMap(Map<String, Object> map) {
        RoleChange rc = new RoleChange();
        rc.setOperator(Boolean.parseBoolean(String.valueOf(map.get("operator"))));
        Object rolesObj = map.get("roles");
        if (rolesObj instanceof List) {
            rc.setRoles((List<String>) rolesObj);
        }
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