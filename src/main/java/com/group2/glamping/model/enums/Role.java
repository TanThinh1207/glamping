package com.group2.glamping.model.enums;

public enum Role {
    ROLE_USER, ROLE_ADMIN, ROLE_MANAGER, ROLE_STAFF;

    public static boolean isValidRole(String roleName) {
        try {
            Role.valueOf(roleName.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }


}
