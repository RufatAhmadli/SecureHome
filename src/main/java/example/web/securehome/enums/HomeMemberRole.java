package example.web.securehome.enums;

public enum HomeMemberRole {
    OWNER,
    ADMIN,
    MEMBER,
    GUEST;

    public boolean canManageRoom() {
        return this == OWNER || this == ADMIN;
    }
}
