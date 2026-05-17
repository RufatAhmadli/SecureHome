"""
SecureHome Class Diagram — exact format matching the provided example image.
Plain white boxes, thin borders, bullet-point attributes/methods, same grid layout.
"""
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
import numpy as np
import pathlib

FW, FH = 28, 24
fig, ax = plt.subplots(figsize=(FW, FH), dpi=150)
ax.set_xlim(0, FW); ax.set_ylim(0, FH)
ax.axis("off")
fig.patch.set_facecolor("white")
ax.set_facecolor("white")
plt.rcParams["font.family"] = "DejaVu Sans"

BK = "#1C2833"   # border / text
WH = "white"
GY = "#F2F3F4"   # very light grey for enum header

# ─────────────────────────────────────────────────────────────────────────
# HELPERS
# ─────────────────────────────────────────────────────────────────────────
LH = 0.285   # line height
PAD = 0.15   # inner padding

def class_box(x, y, name, attrs, methods, stereotype=None, w=3.8):
    """
    Draw a plain UML class box (no colour).
    y = top-left y.  Returns anchor dict.
    """
    name_h  = 0.48 + (0.26 if stereotype else 0)
    attr_h  = max(len(attrs), 1) * LH + PAD * 2
    meth_h  = len(methods) * LH + PAD * 2 if methods else PAD
    total_h = name_h + attr_h + meth_h

    # outer box
    ax.add_patch(mpatches.Rectangle(
        (x, y - total_h), w, total_h,
        linewidth=0.9, edgecolor=BK, facecolor=WH, zorder=2))

    # ── name section ──
    ty = y - PAD * 0.6
    if stereotype:
        ax.text(x + w/2, ty, stereotype,
                ha="center", va="top", fontsize=6.5,
                fontstyle="italic", color=BK, zorder=3)
        ty -= 0.26
    ax.text(x + w/2, ty, name,
            ha="center", va="top", fontsize=8.2,
            fontweight="bold", color=BK, zorder=3)

    # divider after name
    d1 = y - name_h
    ax.plot([x, x+w], [d1, d1], color=BK, lw=0.7, zorder=3)

    # ── attributes ──
    ay = d1 - PAD
    for a in attrs:
        ax.text(x + 0.18, ay, f"\u2022 {a}",
                ha="left", va="top", fontsize=6.6, color=BK, zorder=3)
        ay -= LH

    # divider after attributes
    d2 = d1 - attr_h
    if methods:
        ax.plot([x, x+w], [d2, d2], color=BK, lw=0.7, zorder=3)
        my = d2 - PAD
        for m in methods:
            ax.text(x + 0.18, my, f"\u2022 {m}",
                    ha="left", va="top", fontsize=6.6, color=BK, zorder=3)
            my -= LH

    cx = x + w/2
    return dict(top=(cx, y), bot=(cx, y-total_h),
                left=(x, y-total_h/2), right=(x+w, y-total_h/2),
                tl=(x, y), tr=(x+w, y),
                x=x, y=y, w=w, h=total_h)


def enum_box(x, y, name, values, w=3.2):
    """Draw an <<enumeration>> box with light-grey header."""
    hdr_h   = 0.60
    body_h  = len(values) * LH + PAD * 2
    total_h = hdr_h + body_h

    ax.add_patch(mpatches.Rectangle(
        (x, y-total_h), w, total_h,
        linewidth=0.9, edgecolor=BK, facecolor=WH, zorder=2))
    ax.add_patch(mpatches.Rectangle(
        (x, y-hdr_h), w, hdr_h,
        linewidth=0, facecolor=GY, zorder=2))
    ax.plot([x, x+w], [y-hdr_h, y-hdr_h], color=BK, lw=0.7, zorder=3)

    ax.text(x+w/2, y-0.08, "<<enumeration>>",
            ha="center", va="top", fontsize=6.2,
            fontstyle="italic", color=BK, zorder=3)
    ax.text(x+w/2, y-0.34, name,
            ha="center", va="top", fontsize=7.8,
            fontweight="bold", color=BK, zorder=3)

    vy = y - hdr_h - PAD
    for v in values:
        ax.text(x+w/2, vy, v,
                ha="center", va="top", fontsize=6.6, color=BK, zorder=3)
        vy -= LH

    cx = x+w/2
    return dict(top=(cx, y), bot=(cx, y-total_h),
                left=(x, y-total_h/2), right=(x+w, y-total_h/2),
                x=x, y=y, w=w, h=total_h)


def line(p1, p2, lw=0.75, ls="-", color=BK):
    ax.plot([p1[0], p2[0]], [p1[1], p2[1]],
            color=color, lw=lw, linestyle=ls, zorder=1)


def arrow_line(p1, p2, lw=0.75, ls="-"):
    """Simple line with open arrowhead at p2."""
    ax.annotate("", xy=p2, xytext=p1,
        arrowprops=dict(
            arrowstyle="->,head_width=0.18,head_length=0.22",
            color=BK, lw=lw, linestyle=ls,
            connectionstyle="arc3,rad=0.0"), zorder=2)


def inherit_line(child, parent):
    """Hollow-triangle inheritance: child → parent."""
    ax.annotate("", xy=parent, xytext=child,
        arrowprops=dict(
            arrowstyle="-|>",
            color=BK, lw=0.85, mutation_scale=11,
            connectionstyle="arc3,rad=0.0"), zorder=2)


def dashed_arrow(p1, p2, lw=0.75):
    ax.annotate("", xy=p2, xytext=p1,
        arrowprops=dict(
            arrowstyle="->,head_width=0.15,head_length=0.20",
            color=BK, lw=lw, linestyle="dashed",
            connectionstyle="arc3,rad=0.0"), zorder=2)


def mult(x, y, text, fs=6.5):
    ax.text(x, y, text, ha="center", va="center",
            fontsize=fs, color=BK, zorder=4)


# ═══════════════════════════════════════════════════════════════════════════
# ROW 1 — TOP  (y_top = 23.2)
# ═══════════════════════════════════════════════════════════════════════════
R1Y = 23.2
W1  = 3.6

User = class_box(0.2, R1Y, "User",
    attrs=["id: Long",
           "email: String",
           "firstName: String",
           "lastName: String",
           "password: String"],
    methods=["register(): void",
             "login(): JWT",
             "changePassword(): void",
             "deleteAccount(): void"],
    w=W1)

Role = class_box(4.3, R1Y, "Role",
    attrs=["id: Long",
           "roleName: String"],
    methods=["assignRole(): void"],
    w=2.8)

Home = class_box(7.6, R1Y, "Home",
    attrs=["id: Long",
           "name: String",
           "address: String",
           "city: String",
           "timezone: String"],
    methods=["create(): void",
             "update(): void",
             "delete(): void"],
    w=W1)

Room = class_box(12.3, R1Y, "Room",
    attrs=["id: Long",
           "roomName: String",
           "floor: Integer",
           "description: String"],
    methods=["addDevice(): void",
             "removeDevice(): void"],
    w=W1)

Device = class_box(17.2, R1Y, "Device",
    attrs=["id: Long",
           "deviceName: String",
           "displayName: String",
           "protocol: CommunicationProtocol",
           "status: DeviceStatus"],
    methods=["setStatus(): void",
             "sendCommand(): Result"],
    stereotype="<<abstract>>",
    w=W1+0.4)

# ═══════════════════════════════════════════════════════════════════════════
# ROW 2 — MIDDLE  (y_top = 14.5)
# ═══════════════════════════════════════════════════════════════════════════
R2Y = 14.8

UserProfile = class_box(0.2, R2Y, "UserProfile",
    attrs=["id: Long",
           "phoneNumber: String",
           "address: String",
           "birthDate: LocalDate",
           "emailNotifications: Boolean",
           "smsNotifications: Boolean"],
    methods=["update(): void"],
    w=W1)

HomeMember = class_box(4.3, R2Y, "HomeMember",
    attrs=["id: Long",
           "role: HomeMemberRole"],
    methods=["updateRole(): void"],
    w=3.0)

ActivityLog = class_box(7.8, R2Y, "ActivityLog",
    attrs=["id: Long",
           "actor: String",
           "category: String",
           "action: String",
           "description: String",
           "homeId: Long",
           "occurredAt: LocalDateTime"],
    methods=["filterByDevice(): List",
             "filterByRange(): List"],
    w=W1+0.2)

JwtService = class_box(12.5, R2Y, "JwtService",
    attrs=["secretKey: String",
           "expirationTime: Long"],
    methods=["generateToken(): String",
             "extractUsername(): String",
             "isTokenExpired(): Boolean"],
    w=W1)

DevCommandRouter = class_box(17.2, R2Y, "DeviceCommandRouter",
    attrs=[],
    methods=["route(): void",
             "handleStateChange(): void",
             "broadcastToHome(): void"],
    w=W1+0.4)

# ═══════════════════════════════════════════════════════════════════════════
# ROW 3 — DEVICE SUBCLASSES  (y_top = 7.8)
# ═══════════════════════════════════════════════════════════════════════════
R3Y = 7.8

Camera = class_box(12.5, R3Y, "Camera",
    attrs=["resolution: String",
           "motionDetection: Boolean",
           "nightVision: Boolean",
           "armed: Boolean"],
    methods=["arm(): void",
             "disarm(): void",
             "reportArmedStatus(): void"],
    w=W1)

SmartLock = class_box(17.2, R3Y, "SmartLock",
    attrs=["lockStatus: LockStatus",
           "autoLock: Boolean",
           "autoLockDelay: int",
           "tamperAlert: Boolean"],
    methods=["lock(): void",
             "unlock(): void",
             "reportLockStatus(): void"],
    w=W1+0.4)

# ProtocolAdapter interface (right of DevCommandRouter)
ProtocolAdapter = class_box(23.0, R2Y, "ProtocolAdapter",
    attrs=[],
    methods=["normalize(): DeviceCommand",
             "supports(): Protocol"],
    stereotype="<<interface>>",
    w=3.2)

MqttAdapter = class_box(23.0, R3Y, "MqttAdapter",
    attrs=[],
    methods=["normalize(): DeviceCommand",
             "supports(): Protocol"],
    w=3.2)

# ═══════════════════════════════════════════════════════════════════════════
# ROW 4 — ENUMERATIONS  (y_top = 3.0)
# ═══════════════════════════════════════════════════════════════════════════
R4Y = 3.2

E_Role = enum_box(0.2, R4Y, "HomeMemberRole",
    ["OWNER", "ADMIN", "MEMBER", "GUEST",
     "canManageRoom(): bool",
     "canManageDevice(): bool",
     "canOperateDevice(): bool"],
    w=3.5)

E_Status = enum_box(4.3, R4Y, "DeviceStatus",
    ["ONLINE", "OFFLINE",
     "ERROR", "MAINTENANCE", "INITIALIZING"],
    w=3.2)

E_Lock = enum_box(8.0, R4Y, "LockStatus",
    ["LOCKED", "UNLOCKED", "JAMMED"],
    w=2.8)

E_Proto = enum_box(11.4, R4Y, "CommunicationProtocol",
    ["MQTT", "MATTER", "HTTP"],
    w=3.2)

# ═══════════════════════════════════════════════════════════════════════════
# RELATIONSHIPS
# ═══════════════════════════════════════════════════════════════════════════

# ── User *--* Role ────────────────────────────────────────────────────────
line(User["right"], Role["left"])
mult(User["right"][0]+0.25, User["right"][1]+0.18, "*")
mult(Role["left"][0]-0.25,  Role["left"][1]+0.18,  "*")

# ── User 1--1 UserProfile ─────────────────────────────────────────────────
line(User["bot"], UserProfile["top"])
mult(User["bot"][0]+0.20,      User["bot"][1]-0.18,      "1")
mult(UserProfile["top"][0]+0.20, UserProfile["top"][1]+0.18, "1")

# ── Home 1--* Room ────────────────────────────────────────────────────────
line(Home["right"], Room["left"])
mult(Home["right"][0]+0.22, Home["right"][1]+0.18, "1")
mult(Room["left"][0]-0.22,  Room["left"][1]+0.18,  "*")

# ── Home 1--* HomeMember ──────────────────────────────────────────────────
p1 = (Home["x"]+Home["w"]*0.4, Home["y"]-Home["h"])
line(p1, HomeMember["top"])
mult(p1[0]+0.20, p1[1]-0.18, "1")
mult(HomeMember["top"][0]+0.20, HomeMember["top"][1]+0.18, "*")

# ── User 1--* HomeMember ─────────────────────────────────────────────────
p1u = (User["x"]+User["w"]*0.7, User["y"]-User["h"])
line(p1u, (HomeMember["top"][0]-0.3, HomeMember["top"][1]))
mult(p1u[0]+0.20, p1u[1]-0.18, "1")
mult(HomeMember["top"][0]-0.10, HomeMember["top"][1]+0.18, "*")

# ── Home 1--* Device (via Device.home) ────────────────────────────────────
p_hd = (Home["x"]+Home["w"], Home["y"]-Home["h"]*0.4)
p_dv = (Device["x"], Device["y"]-Device["h"]*0.55)
line(p_hd, p_dv)
mult(p_hd[0]+0.18, p_hd[1]+0.15, "1")
mult(p_dv[0]-0.18, p_dv[1]+0.15, "*")

# ── Room 1--* Device ─────────────────────────────────────────────────────
line(Room["right"], (Device["x"], Device["y"]-Device["h"]*0.35))
mult(Room["right"][0]+0.20, Room["right"][1]+0.18, "1")
mult(Device["left"][0]-0.18, Device["left"][1]+0.18, "*")

# ── ActivityLog *--1 Home ─────────────────────────────────────────────────
p_al = (ActivityLog["top"][0]-0.4, ActivityLog["top"][1])
p_hm = (Home["x"]+Home["w"]*0.5, Home["y"]-Home["h"])
line(p_al, p_hm)
mult(p_al[0]-0.18, p_al[1]+0.18, "*")
mult(p_hm[0]+0.18, p_hm[1]-0.18, "1")

# ── Camera extends Device ────────────────────────────────────────────────
inherit_line(Camera["top"], (Device["x"]+Device["w"]*0.35, Device["y"]-Device["h"]))

# ── SmartLock extends Device ──────────────────────────────────────────────
inherit_line(SmartLock["top"], (Device["x"]+Device["w"]*0.65, Device["y"]-Device["h"]))

# ── MqttAdapter implements ProtocolAdapter ────────────────────────────────
inherit_line(MqttAdapter["top"], ProtocolAdapter["bot"])

# ── DevCommandRouter ..> Camera / SmartLock ──────────────────────────────
dashed_arrow((DevCommandRouter["x"], DevCommandRouter["y"]-DevCommandRouter["h"]),
             Camera["right"])
dashed_arrow((DevCommandRouter["x"]+DevCommandRouter["w"]*0.6,
              DevCommandRouter["y"]-DevCommandRouter["h"]),
             SmartLock["top"])

# ── JwtService ..> User ───────────────────────────────────────────────────
dashed_arrow((JwtService["x"], JwtService["y"]-JwtService["h"]*0.5),
             (User["x"]+User["w"], User["y"]-User["h"]*0.7))

# ── HomeMember -- HomeMemberRole (dashed) ─────────────────────────────────
line(HomeMember["bot"],
     (E_Role["top"][0]+0.3, E_Role["top"][1]),
     ls="--")

# ── Device -- DeviceStatus (dashed) ──────────────────────────────────────
line((Device["x"]+Device["w"]*0.3, Device["y"]-Device["h"]),
     (E_Status["top"][0]+0.3, E_Status["top"][1]),
     ls="--")

# ── SmartLock -- LockStatus (dashed) ─────────────────────────────────────
line((SmartLock["x"]+SmartLock["w"]*0.3, SmartLock["y"]-SmartLock["h"]),
     (E_Lock["top"][0]+0.3, E_Lock["top"][1]),
     ls="--")

# ── Device -- CommunicationProtocol (dashed) ─────────────────────────────
line((Device["x"]+Device["w"]*0.55, Device["y"]-Device["h"]),
     (E_Proto["top"][0]+0.6, E_Proto["top"][1]),
     ls="--")

# ── MqttAdapter ..> DevCommandRouter ─────────────────────────────────────
dashed_arrow(MqttAdapter["left"], DevCommandRouter["right"])

# ── title ─────────────────────────────────────────────────────────────────
ax.text(FW/2, FH-0.3,
        "SecureHome — Class Diagram",
        ha="center", va="top", fontsize=13,
        fontweight="bold", color=BK)

# ── save ─────────────────────────────────────────────────────────────────
out = pathlib.Path(
    r"c:\Users\user\MyProjects\SecureHome\docs\SecureHome_ClassDiagram.png")
out.parent.mkdir(parents=True, exist_ok=True)
fig.savefig(out, dpi=150, bbox_inches="tight",
            facecolor=fig.get_facecolor())
print(f"Saved: {out}")
plt.close(fig)
