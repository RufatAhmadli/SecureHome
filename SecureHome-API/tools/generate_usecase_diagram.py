"""
SecureHome — Updated Use Case Diagram (v3 — actor generalization + minimal lines)
Each actor only connects to use cases it UNIQUELY adds beyond the role below it.
Actor hierarchy arrows carry the rest.
"""

import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
from matplotlib.patches import Ellipse
import numpy as np, pathlib

FW, FH = 32, 26
fig, ax = plt.subplots(figsize=(FW, FH), dpi=150)
ax.set_xlim(0, FW); ax.set_ylim(0, FH)
ax.axis("off")
fig.patch.set_facecolor("#F5F8FA")
ax.set_facecolor("#F5F8FA")
plt.rcParams["font.family"] = "DejaVu Sans"

# Palette
C = dict(
    owner   = "#1A5276",
    hadmin  = "#1E8449",
    member  = "#148F77",
    guest   = "#626567",
    sysadm  = "#6C3483",
    allusr  = "#D35400",
    inc     = "#0E6655",
    dark    = "#1C2833",
    line    = "#AAB7B8",
    arr     = "#C0392B",
    white   = "white",
)

# ── helpers ───────────────────────────────────────────────────────────────

def uc_el(x, y, label, role, w=2.6, h=0.56, fs=7.0):
    fc = C[role]
    el = Ellipse((x, y), width=w, height=h,
                 facecolor=fc, edgecolor="#0D0D0D", linewidth=0.7, zorder=3)
    ax.add_patch(el)
    ax.text(x, y, label, ha="center", va="center", fontsize=fs,
            color=C["white"], fontweight="bold", zorder=4,
            multialignment="center")
    return (x, y)


def inc_el(x, y, label, w=2.6, h=0.56, fs=6.8):
    return uc_el(x, y, label, "inc", w=w, h=h, fs=fs)


def actor_fig(x, y, label, role, fs=8.5):
    col = C[role]
    r = 0.30
    ax.add_patch(plt.Circle((x, y+1.18), r, color=col, zorder=6))
    ax.plot([x,x],          [y+0.88, y+0.22], color=col, lw=2.2, zorder=6)
    ax.plot([x-0.44, x+0.44],[y+0.62, y+0.62], color=col, lw=2.2, zorder=6)
    ax.plot([x, x-0.40],    [y+0.22, y-0.30], color=col, lw=2.2, zorder=6)
    ax.plot([x, x+0.40],    [y+0.22, y-0.30], color=col, lw=2.2, zorder=6)
    ax.text(x, y-0.60, label, ha="center", va="top", fontsize=fs,
            color=col, fontweight="bold", multialignment="center", zorder=6)
    return (x, y+0.55)


def link(p1, p2, role, lw=0.85):
    ax.plot([p1[0],p2[0]], [p1[1],p2[1]],
            color=C[role], lw=lw, zorder=2)


def inc_arr(src, dst):
    ax.annotate("", xy=dst, xytext=src,
        arrowprops=dict(arrowstyle="->", color=C["arr"], lw=1.1,
                        linestyle="dashed",
                        connectionstyle="arc3,rad=0.0"), zorder=3)
    mx, my = (src[0]+dst[0])/2, (src[1]+dst[1])/2
    ax.text(mx, my+0.18, "<<include>>", ha="center", va="bottom",
            fontsize=5.6, color=C["arr"], style="italic", zorder=5)


def gen_arr(child, parent):
    """Hollow-head generalisation arrow child -> parent."""
    ax.annotate("", xy=parent, xytext=child,
        arrowprops=dict(arrowstyle="-|>", color=C["dark"], lw=1.5,
                        mutation_scale=16,
                        connectionstyle="arc3,rad=0.0"), zorder=5)


def band(x0,x1,y0,y1, title, fill, alpha=0.10):
    r = mpatches.FancyBboxPatch((x0,y0), x1-x0, y1-y0,
        boxstyle="round,pad=0.12", linewidth=1.0,
        edgecolor="#AAB7B8", facecolor=fill, alpha=alpha, zorder=1)
    ax.add_patch(r)
    ax.text((x0+x1)/2, y1-0.12, title, ha="center", va="top",
            fontsize=7.5, color=C["dark"], fontweight="bold", zorder=2)

# ═══════════════════════════════════════════════════════════════════════════
# SYSTEM BOUNDARY
# ═══════════════════════════════════════════════════════════════════════════
SX0,SX1,SY0,SY1 = 3.1,29.8,0.4,25.5
ax.add_patch(mpatches.FancyBboxPatch((SX0,SY0), SX1-SX0, SY1-SY0,
    boxstyle="round,pad=0.2", linewidth=2.8,
    edgecolor=C["dark"], facecolor="white", zorder=0))
ax.text((SX0+SX1)/2, SY1-0.18, "SecureHome System",
        ha="center", va="top", fontsize=14, color=C["dark"],
        fontweight="bold", zorder=1)

# swimlane backgrounds
band(3.3, 10.1,  0.6, 25.2, "Account & Authentication",   "#AED6F1", 0.12)
band(10.3,18.0,  0.6, 25.2, "Home, Room & Device Mgmt",   "#A9DFBF", 0.12)
band(18.2,23.4,  0.6, 25.2, "Member Mgmt & System",       "#FAD7A0", 0.12)
band(23.6,29.6,  0.6, 25.2, "<<include>> Sub-flows",      "#D2B4DE", 0.12)

# ═══════════════════════════════════════════════════════════════════════════
# ACTORS — left column, stacked with generalisation chain
# ═══════════════════════════════════════════════════════════════════════════
# Actor positions (waist centre x=1.4)
AX = 1.4
a_owner  = actor_fig(AX, 22.5, "Owner",       "owner")
a_hadmin = actor_fig(AX, 18.0, "Home\nAdmin", "hadmin")
a_member = actor_fig(AX, 13.0, "Member",      "member")
a_guest  = actor_fig(AX,  8.0, "Guest",       "guest")
a_sysadm = actor_fig(AX,  2.5, "System\nAdmin","sysadm")
a_alluse = actor_fig(31.2,12.5,"All\nUsers",  "allusr")

# Generalisation arrows (child bottom → parent top)
gen_arr((AX, 17.4), (AX, 16.5))    # Home Admin → Member gap
gen_arr((AX, 12.4), (AX, 11.5))    # Member → Guest gap
ax.text(AX-0.65, 17.0, "extends", fontsize=6.5, color=C["dark"],
        rotation=90, va="center", style="italic")
ax.text(AX-0.65, 12.0, "extends", fontsize=6.5, color=C["dark"],
        rotation=90, va="center", style="italic")

# ═══════════════════════════════════════════════════════════════════════════
# USE CASES
# ═══════════════════════════════════════════════════════════════════════════

# ── Auth / All Users  (x ≈ 6.7) ──────────────────────────────────────────
X1 = 6.7
r_reg    = uc_el(X1,24.2,"Register Account",        "allusr")
r_login  = uc_el(X1,23.1,"Login",                   "allusr")
r_logout = uc_el(X1,22.0,"Logout",                  "allusr")
r_chgpwd = uc_el(X1,20.9,"Change Password",          "allusr")
r_prof   = uc_el(X1,19.7,"View / Update\nProfile",  "allusr", h=0.65)
r_delacc = uc_el(X1,18.5,"Delete My Account",        "allusr")
r_myhome = uc_el(X1,17.3,"View My Homes\n& Memberships","allusr", h=0.65)

# ── Home Mgmt — Owner only  (x ≈ 14.1) ──────────────────────────────────
X2 = 14.1
r_crhome = uc_el(X2,24.2,"Create Home",              "owner")
r_uphome = uc_el(X2,23.1,"Update Home\nSettings",    "owner", h=0.65)
r_delhome= uc_el(X2,21.9,"Delete Home",              "owner")

# ── Room & Device — Owner + Home Admin ───────────────────────────────────
r_crroom = uc_el(X2,20.4,"Create Room",              "hadmin")
r_uproom = uc_el(X2,19.4,"Update Room",              "hadmin")
r_delroom= uc_el(X2,18.4,"Delete Room",              "hadmin")
r_adddev = uc_el(X2,17.1,"Add Device\n(Camera/Lock)","hadmin", h=0.65)
r_updev  = uc_el(X2,16.0,"Update Device\nConfig",    "hadmin", h=0.65)
r_deldev = uc_el(X2,14.9,"Delete Device",            "hadmin")

# ── Device ops — Member, Admin, Owner ────────────────────────────────────
r_arm    = uc_el(X2,13.3,"Arm Camera",               "member")
r_disarm = uc_el(X2,12.3,"Disarm Camera",            "member")
r_lock   = uc_el(X2,11.3,"Lock Smart Lock",          "member")
r_unlock = uc_el(X2,10.3,"Unlock Smart Lock",        "member")

# ── Read-only — all roles  ────────────────────────────────────────────────
r_viewdev= uc_el(X2, 8.8,"View Devices & Status",    "guest")
r_viewrm = uc_el(X2, 7.8,"View Rooms",               "guest")
r_viewhm = uc_el(X2, 6.8,"View Home Details",        "guest")

# ── Member Mgmt & System  (x ≈ 20.8) ─────────────────────────────────────
X3 = 20.8
r_updrol = uc_el(X3,24.2,"Update Member\nRole",      "owner",  h=0.65)
r_remmem = uc_el(X3,23.0,"Remove Member",            "owner")
r_addmem = uc_el(X3,21.6,"Add Member\nto Home",      "hadmin", h=0.65)
r_viewmm = uc_el(X3,20.2,"View Members",             "guest")
r_viewhd = uc_el(X3,18.8,"View Home Details\n(full)","guest",  h=0.65)
r_actlog = uc_el(X3,17.2,"View Activity Logs",       "hadmin")

r_allhom = uc_el(X3,14.5,"View All Homes\n(System-Wide)","sysadm", h=0.65)
r_allprf = uc_el(X3,13.3,"View All User\nProfiles",  "sysadm", h=0.65)

# ═══════════════════════════════════════════════════════════════════════════
# <<include>> targets  (x ≈ 26.7)
# ═══════════════════════════════════════════════════════════════════════════
X4 = 26.7
i_hash   = inc_el(X4,24.2,"Hash Password")
i_valcrd = inc_el(X4,23.1,"Validate Credentials")
i_genjwt = inc_el(X4,22.0,"Generate JWT Token")
i_veripw = inc_el(X4,20.8,"Verify Current\nPassword",  h=0.65)
i_autoow = inc_el(X4,19.5,"Auto-Assign\nOwner Membership", h=0.65)
i_ownrol = inc_el(X4,18.2,"Verify Owner Role")
i_casdel = inc_el(X4,16.9,"Cascade Delete\nRooms/Devices", h=0.65)
i_owntrf = inc_el(X4,15.6,"Ownership\nTransfer Logic",     h=0.65)
i_tgusr  = inc_el(X4,14.3,"Verify Target\nUser Exists",    h=0.65)
i_oprgt  = inc_el(X4,13.0,"Verify Operation\nRights",      h=0.65)
i_hmemb  = inc_el(X4,11.7,"Verify Home\nMembership",       h=0.65)
i_jwtv   = inc_el(X4,10.5,"Verify JWT Token")
i_pubev  = inc_el(X4, 9.3,"Publish Activity\nEvent",       h=0.65)

# ═══════════════════════════════════════════════════════════════════════════
# ACTOR → USE-CASE  (each actor ONLY to its unique-level UCs)
# ═══════════════════════════════════════════════════════════════════════════

# All Users (right) → auth use cases
for r in [r_reg,r_login,r_logout,r_chgpwd,r_prof,r_delacc,r_myhome]:
    link(a_alluse, r, "allusr")

# Owner uniquely: home management + owner-only member ops
for r in [r_crhome,r_uphome,r_delhome, r_updrol,r_remmem]:
    link(a_owner, r, "owner")

# Home Admin uniquely: room/device management + add member + activity log
for r in [r_crroom,r_uproom,r_delroom,r_adddev,r_updev,r_deldev,
          r_addmem, r_actlog]:
    link(a_hadmin, r, "hadmin")

# Member uniquely: device operations
for r in [r_arm,r_disarm,r_lock,r_unlock]:
    link(a_member, r, "member")

# Guest uniquely: read-only views
for r in [r_viewdev,r_viewrm,r_viewhm, r_viewmm,r_viewhd]:
    link(a_guest, r, "guest")

# System Admin
for r in [r_allhom,r_allprf]:
    link(a_sysadm, r, "sysadm")

# ═══════════════════════════════════════════════════════════════════════════
# <<include>> ARROWS
# ═══════════════════════════════════════════════════════════════════════════
inc_arr(r_reg,    i_hash)
inc_arr(r_login,  i_valcrd)
inc_arr(r_login,  i_genjwt)
inc_arr(r_chgpwd, i_veripw)
inc_arr(r_crhome, i_autoow)
inc_arr(r_uphome, i_ownrol)
inc_arr(r_delhome,i_ownrol)
inc_arr(r_delhome,i_casdel)
inc_arr(r_updrol, i_owntrf)
inc_arr(r_addmem, i_tgusr)
inc_arr(r_arm,    i_oprgt)
inc_arr(r_lock,   i_oprgt)
inc_arr(r_myhome, i_hmemb)
inc_arr(r_viewhm, i_hmemb)
inc_arr(r_login,  i_jwtv)
inc_arr(r_crhome, i_pubev)
inc_arr(r_adddev, i_pubev)

# ═══════════════════════════════════════════════════════════════════════════
# LEGEND
# ═══════════════════════════════════════════════════════════════════════════
lx, ly = 0.1, 5.5
ax.text(lx+0.1, ly+4.4, "Legend", fontsize=9, fontweight="bold", color=C["dark"])
entries = [
    ("owner",  "Owner"),
    ("hadmin", "Home Admin"),
    ("member", "Member"),
    ("guest",  "Guest (read-only)"),
    ("sysadm", "System Admin"),
    ("allusr", "All Users (auth)"),
    ("inc",    "<<include>> sub-flow"),
]
for i,(role,lbl) in enumerate(entries):
    ey = ly + 3.9 - i*0.52
    el = Ellipse((lx+0.55, ey), width=1.05, height=0.34,
                 facecolor=C[role], edgecolor="#0D0D0D", lw=0.5, zorder=6)
    ax.add_patch(el)
    ax.text(lx+1.20, ey, lbl, fontsize=7, va="center", color=C["dark"])

ax.annotate("", xy=(lx+0.95, ly-0.25), xytext=(lx+0.15, ly-0.25),
    arrowprops=dict(arrowstyle="->", color=C["arr"], lw=1.0, linestyle="dashed"))
ax.text(lx+1.20, ly-0.25, "<<include>> link", fontsize=7, va="center", color=C["dark"])

ax.plot([lx+0.15,lx+0.95],[ly-0.70,ly-0.70], color=C["line"], lw=1.2)
ax.text(lx+1.20, ly-0.70, "Actor - Use Case link", fontsize=7, va="center", color=C["dark"])

ax.annotate("", xy=(lx+0.95, ly-1.15), xytext=(lx+0.15, ly-1.15),
    arrowprops=dict(arrowstyle="-|>", color=C["dark"], lw=1.3, mutation_scale=12))
ax.text(lx+1.20, ly-1.15, "Actor generalisation (extends)", fontsize=7, va="center", color=C["dark"])

# ── title ─────────────────────────────────────────────────────────────────
ax.text(FW/2, 25.75, "SecureHome - Updated Use Case Diagram",
        ha="center", va="center", fontsize=16, fontweight="bold", color=C["dark"])

# ── save ─────────────────────────────────────────────────────────────────
out = pathlib.Path(r"c:\Users\user\MyProjects\SecureHome\docs\SecureHome_UseCaseDiagram.png")
out.parent.mkdir(parents=True, exist_ok=True)
fig.savefig(out, dpi=150, bbox_inches="tight", facecolor=fig.get_facecolor())
print(f"Saved: {out}")
plt.close(fig)
