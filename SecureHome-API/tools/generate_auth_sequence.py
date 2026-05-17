"""
SecureHome — Auth Management Sequence Diagram
5.4.1 System Requirements traced with SR badges.
Original 3-section structure preserved.
"""

import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
from matplotlib.patches import FancyBboxPatch
import pathlib

FW, FH = 28, 44
fig, ax = plt.subplots(figsize=(FW, FH), dpi=150)
ax.set_xlim(0, FW); ax.set_ylim(0, FH)
ax.axis("off")
fig.patch.set_facecolor("white")
ax.set_facecolor("white")
plt.rcParams["font.family"] = "DejaVu Sans"

C_PART  = "#1A5276"
C_LIFE  = "#AAB7B8"
C_MSG   = "#1C2833"
C_RET   = "#5D6D7E"
C_ACT   = "#D6EAF8"
C_ACT_B = "#2471A3"
C_ALT_B = "#E74C3C"
C_NOTE  = "#FEF9E7"
C_NOTE_B= "#F39C12"
C_SELF  = "#1A5276"

PARTS = [
    ("User",             2.0),
    ("Login\nPage",      6.0),
    ("Spring Boot\nAPI", 11.5),
    ("JWT\nService",     16.5),
    ("Local\nDB",        21.5),
    ("Audit\nLogger",    26.0),
]
PART_Y = FH - 1.5
BOX_W, BOX_H = 2.2, 0.9

PX = {n.split("\n")[0]: x for n, x in PARTS}
PX["Login"] = 6.0
PX["Spring"] = 11.5
PX["JWT"]    = 16.5
PX["Local"]  = 21.5
PX["Audit"]  = 26.0

for name, x in PARTS:
    ax.add_patch(FancyBboxPatch(
        (x-BOX_W/2, PART_Y-BOX_H/2), BOX_W, BOX_H,
        boxstyle="round,pad=0.05",
        facecolor=C_PART, edgecolor="white", linewidth=1.5, zorder=4))
    ax.text(x, PART_Y, name, ha="center", va="center",
            fontsize=7.5, color="white", fontweight="bold",
            multialignment="center", zorder=5)

LIFELINE_TOP = PART_Y - BOX_H/2
LIFELINE_BOT = 0.4
for _, x in PARTS:
    ax.plot([x, x], [LIFELINE_TOP, LIFELINE_BOT],
            color=C_LIFE, lw=1.0, linestyle="--", zorder=1)

# ── helpers ───────────────────────────────────────────────────────────────

def msg(x1, x2, y, label, ret=False, color=None, lw=1.3, fs=6.8):
    c = color or (C_RET if ret else C_MSG)
    if abs(x2-x1) < 0.01: return
    ax.annotate("", xy=(x2, y), xytext=(x1, y),
        arrowprops=dict(arrowstyle="->,head_width=0.18,head_length=0.22",
                        color=c, lw=lw, linestyle="--" if ret else "-",
                        connectionstyle="arc3,rad=0.0"), zorder=3)
    ax.text((x1+x2)/2, y+0.18, label, ha="center", va="bottom",
            fontsize=fs, color=c,
            fontstyle="italic" if ret else "normal",
            fontweight="bold" if not ret else "normal",
            multialignment="center", zorder=4)


def self_msg(x, y, label, dy=0.5, fs=6.8):
    ax.annotate("", xy=(x, y-dy), xytext=(x, y),
        arrowprops=dict(arrowstyle="->,head_width=0.15,head_length=0.18",
                        color=C_SELF, lw=1.2,
                        connectionstyle="arc,angleA=-30,angleB=30,rad=0.3"),
        zorder=3)
    ax.text(x+0.72, y-dy/2, label, ha="left", va="center",
            fontsize=fs, color=C_SELF, fontweight="bold", zorder=4)


def act(x, y_top, y_bot, w=0.22):
    ax.add_patch(FancyBboxPatch(
        (x-w/2, y_bot), w, y_top-y_bot,
        boxstyle="square,pad=0.0",
        facecolor=C_ACT, edgecolor=C_ACT_B, linewidth=0.8, zorder=2))


def frame(x0, y_top, x1, y_bot, tag, label, fc="#FDFEFE", ec=C_ALT_B):
    ax.add_patch(mpatches.FancyBboxPatch(
        (x0, y_bot), x1-x0, y_top-y_bot,
        boxstyle="square,pad=0.0", facecolor=fc, edgecolor=ec,
        linewidth=1.3, alpha=0.25, zorder=1))
    tw, th = 0.8, 0.35
    ax.add_patch(FancyBboxPatch(
        (x0, y_top-th), tw, th,
        boxstyle="square,pad=0.0",
        facecolor=ec, edgecolor=ec, linewidth=0, zorder=2))
    ax.text(x0+tw/2, y_top-th/2, tag, ha="center", va="center",
            fontsize=7, color="white", fontweight="bold", zorder=3)
    ax.text(x0+tw+0.12, y_top-th/2, label, ha="left", va="center",
            fontsize=6.8, color=ec, fontweight="bold", zorder=3)


def divider(x0, x1, y, label, lc="#AAB7B8"):
    ax.plot([x0, x1], [y, y], color=lc, lw=0.8, linestyle="--", zorder=2)
    ax.text(x0+0.15, y+0.10, label, fontsize=6.3,
            color=lc, fontstyle="italic", zorder=3)


def note(x, y, text, width=2.8, height=0.55):
    ax.add_patch(FancyBboxPatch(
        (x, y-height/2), width, height,
        boxstyle="round,pad=0.06",
        facecolor=C_NOTE, edgecolor=C_NOTE_B, linewidth=1.0, zorder=3))
    ax.text(x+width/2, y, text, ha="center", va="center",
            fontsize=6.2, color="#7D6608",
            multialignment="center", zorder=4)


def section_label(y, text):
    ax.text(0.1, y, text, ha="left", va="center",
            fontsize=8.5, color="#1A5276", fontweight="bold", zorder=5)
    ax.plot([0.1, FW-0.1], [y-0.20, y-0.20],
            color="#AED6F1", lw=1.0, zorder=1)


def sr(y, tags):
    """SR badge on the right margin."""
    ax.text(FW-0.15, y, tags, ha="right", va="center",
            fontsize=6.2, color="#1A5276", fontweight="bold",
            bbox=dict(boxstyle="round,pad=0.22",
                      facecolor="#EBF5FB", edgecolor="#2471A3",
                      linewidth=0.8),
            zorder=5)


# ═══════════════════════════════════════════════════════════════════════════
# REQUIREMENTS LEGEND  (top-right corner)
# ═══════════════════════════════════════════════════════════════════════════
reqs = [
    ("SR1", "User Authentication — Login + Logout",          "F.R."),
    ("SR2", "Account Lockout — 5 attempts / 15 min",         "F.R."),
    ("SR3", "Password Storage — BCrypt, never plaintext",    "N.F.R."),
    ("SR4", "Audit Logging — login, logout, failed, pwd-chg","N.F.R."),
    ("SR5", "Session Termination — JWT revoked immediately",  "F.R."),
]
rx, ry = 14.5, FH - 0.8
bh = len(reqs)*0.50 + 0.55
ax.add_patch(FancyBboxPatch(
    (rx-0.2, ry-bh), FW-rx+0.05, bh,
    boxstyle="round,pad=0.1",
    facecolor="#EBF5FB", edgecolor="#2471A3", linewidth=1.2, zorder=3))
ax.text(rx + (FW-rx)/2 - 0.1, ry-0.15,
        "System Requirements — §5.4.1",
        ha="center", va="top", fontsize=8,
        fontweight="bold", color="#1A5276", zorder=4)
for i, (code, desc, typ) in enumerate(reqs):
    ty = ry - 0.52 - i*0.50
    ax.text(rx+0.1,  ty, code, ha="left", va="center",
            fontsize=7.2, color="#1A5276", fontweight="bold", zorder=4)
    ax.text(rx+0.85, ty, desc, ha="left", va="center",
            fontsize=6.5, color="#1C2833", zorder=4)
    ax.text(FW-0.3,  ty, typ,  ha="right", va="center",
            fontsize=6.2, color="#7F8C8D", fontstyle="italic", zorder=4)

# ═══════════════════════════════════════════════════════════════════════════
# SECTION 1 — LOGIN SUCCESS
# ═══════════════════════════════════════════════════════════════════════════
section_label(42.0, "1. Login — Success Path")
Y = 41.3

msg(PX["User"], PX["Login"], Y, "enterCredentials(email, password)")
sr(Y, "SR1"); Y -= 0.7

msg(PX["Login"], PX["Spring"], Y,
    "POST /api/v1/auth/login\n{email, password}")
sr(Y, "SR1"); Y -= 0.8

act_api_top = Y + 0.35

msg(PX["Spring"], PX["Local"], Y, "findUserByEmail(email)")
Y -= 0.6
msg(PX["Local"], PX["Spring"], Y,
    "200 User{id, email, passwordHash,\n failedAttempts, lockedUntil}", ret=True)
sr(Y, "SR3"); Y -= 0.85

self_msg(PX["Spring"], Y,
         "checkAccountLock(lockedUntil)  →  not locked", dy=0.55)
sr(Y, "SR2"); Y -= 0.75

self_msg(PX["Spring"], Y,
         "BCrypt.matches(password, hash)  →  true", dy=0.55)
sr(Y, "SR3"); Y -= 0.75

msg(PX["Spring"], PX["Local"], Y, "resetFailedAttempts(userId)")
Y -= 0.6
msg(PX["Local"], PX["Spring"], Y, "200 OK", ret=True); Y -= 0.7

msg(PX["Spring"], PX["JWT"], Y, "generateToken(UserDetails)")
sr(Y, "SR1"); Y -= 0.6
msg(PX["JWT"], PX["Spring"], Y,
    '200 {accessToken: "eyJ...",\n tokenType: "Bearer", expiresIn: 86400}',
    ret=True); Y -= 0.85

msg(PX["Spring"], PX["Audit"], Y,
    "recordLoginEvent(email, ip, timestamp, action=LOGIN_SUCCESS)")
sr(Y, "SR4"); Y -= 0.6
msg(PX["Audit"], PX["Local"], Y, "INSERT INTO audit_log(LOGIN_SUCCESS)")
Y -= 0.5
msg(PX["Local"], PX["Audit"], Y, "saved", ret=True); Y -= 0.45
msg(PX["Audit"], PX["Spring"], Y, "ack()", ret=True); Y -= 0.6

act_api_bot = Y + 0.3
act(PX["Spring"], act_api_top, act_api_bot)

msg(PX["Spring"], PX["Login"], Y,
    '200 OK {accessToken, tokenType: "Bearer"}')
sr(Y, "SR1"); Y -= 0.6

self_msg(PX["Login"], Y,
         'localStorage.setItem("token", accessToken)', dy=0.45)
sr(Y, "SR1"); Y -= 0.65

msg(PX["Login"], PX["User"], Y, "redirectDashboard()")
Y -= 0.5

# ═══════════════════════════════════════════════════════════════════════════
# SECTION 2 — LOGIN FAILURE + LOCKOUT
# ═══════════════════════════════════════════════════════════════════════════
Y -= 0.6
section_label(Y+0.4, "2. Login — Failure Path (Wrong Password / Account Lockout)")
Y -= 0.5

alt_top = Y + 0.1

msg(PX["User"], PX["Login"], Y, "enterCredentials(email, wrongPassword)")
sr(Y, "SR1"); Y -= 0.65
msg(PX["Login"], PX["Spring"], Y,
    "POST /api/v1/auth/login\n{email, wrongPassword}")
Y -= 0.75

msg(PX["Spring"], PX["Local"], Y, "findUserByEmail(email)")
Y -= 0.55
msg(PX["Local"], PX["Spring"], Y,
    "200 User{..., failedAttempts=N}", ret=True); Y -= 0.7

self_msg(PX["Spring"], Y,
         "checkAccountLock()  →  not locked", dy=0.45)
sr(Y, "SR2"); Y -= 0.65

self_msg(PX["Spring"], Y,
         "BCrypt.matches()  →  FALSE", dy=0.45)
sr(Y, "SR3"); Y -= 0.65

msg(PX["Spring"], PX["Local"], Y, "incrementFailedAttempts(userId)")
sr(Y, "SR2"); Y -= 0.55
msg(PX["Local"], PX["Spring"], Y, "failedAttempts=N+1", ret=True); Y -= 0.65

inner_alt_top = Y + 0.1

# [attempts < 5]
msg(PX["Spring"], PX["Audit"], Y,
    "recordLoginEvent(email, LOGIN_FAILED, attempts=N+1)")
sr(Y, "SR4"); Y -= 0.55
msg(PX["Audit"], PX["Local"], Y, "INSERT INTO audit_log(LOGIN_FAILED)")
Y -= 0.45
msg(PX["Local"], PX["Audit"], Y, "saved", ret=True); Y -= 0.4
msg(PX["Audit"], PX["Spring"], Y, "ack()", ret=True); Y -= 0.5
msg(PX["Spring"], PX["Login"], Y,
    '401 Unauthorized\n{error: "Invalid credentials",\n remainingAttempts: 5-(N+1)}')
sr(Y, "SR2"); Y -= 0.75
msg(PX["Login"], PX["User"], Y,
    "showError('Invalid credentials')"); Y -= 0.55

divider(PX["User"]-1.0, PX["Audit"]+1.3, Y,
        "[attempts >= 5  →  lockout]")
Y -= 0.55

msg(PX["Spring"], PX["Local"], Y,
    "lockAccount(userId, lockedUntil = now + 15min)")
sr(Y, "SR2"); Y -= 0.55
msg(PX["Local"], PX["Spring"], Y, "200 OK", ret=True); Y -= 0.55

msg(PX["Spring"], PX["Audit"], Y,
    "recordLoginEvent(email, ACCOUNT_LOCKED, lockedUntil)")
sr(Y, "SR4"); Y -= 0.5
msg(PX["Audit"], PX["Local"], Y, "INSERT INTO audit_log(ACCOUNT_LOCKED)")
Y -= 0.45
msg(PX["Local"], PX["Audit"], Y, "saved", ret=True); Y -= 0.4
msg(PX["Audit"], PX["Spring"], Y, "ack()", ret=True); Y -= 0.5
msg(PX["Spring"], PX["Login"], Y,
    '423 Locked\n{error: "Account locked",\n unlockAt: <timestamp>}')
sr(Y, "SR2"); Y -= 0.75
msg(PX["Login"], PX["User"], Y,
    "showError('Account locked for 15 minutes')"); Y -= 0.55

inner_alt_bot = Y + 0.2
frame(PX["User"]-1.0, inner_alt_top, PX["Audit"]+1.3, inner_alt_bot,
      "alt", "[attempts < 5] / [attempts >= 5  →  lockout]",
      fc="#FDEDEC", ec="#E74C3C")

alt_bot = Y + 0.1
frame(PX["User"]-1.2, alt_top, PX["Audit"]+1.5, alt_bot,
      "alt", "Wrong Password",
      fc="#FDFEFE", ec="#C0392B")

# ═══════════════════════════════════════════════════════════════════════════
# SECTION 3 — LOGOUT / SESSION TERMINATION
# ═══════════════════════════════════════════════════════════════════════════
Y -= 0.8
section_label(Y+0.4, "3. Logout — Session Termination")
Y -= 0.5

msg(PX["User"], PX["Login"], Y, "clickLogout()")
sr(Y, "SR1, SR5"); Y -= 0.65

msg(PX["Login"], PX["Spring"], Y,
    "POST /api/v1/auth/logout\n(Authorization: Bearer <token>)")
sr(Y, "SR5"); Y -= 0.8

msg(PX["Spring"], PX["JWT"], Y, "validateToken(token)")
Y -= 0.55
msg(PX["JWT"], PX["Spring"], Y,
    "200 valid / UserDetails{email}", ret=True); Y -= 0.65

msg(PX["Spring"], PX["JWT"], Y,
    "invalidateToken(token)")
sr(Y, "SR5"); Y -= 0.55
msg(PX["JWT"], PX["Spring"], Y,
    "tokenRevoked()  →  session invalidated immediately", ret=True)
sr(Y, "SR5"); Y -= 0.65

msg(PX["Spring"], PX["Audit"], Y,
    "recordLogoutEvent(email, sessionEnd, action=LOGOUT)")
sr(Y, "SR4"); Y -= 0.55
msg(PX["Audit"], PX["Local"], Y,
    "INSERT INTO audit_log(LOGOUT, sessionDuration)")
Y -= 0.45
msg(PX["Local"], PX["Audit"], Y, "saved", ret=True); Y -= 0.4
msg(PX["Audit"], PX["Spring"], Y, "ack()", ret=True); Y -= 0.55

msg(PX["Spring"], PX["Login"], Y,
    '200 OK {message: "Logged out"}')
Y -= 0.65

self_msg(PX["Login"], Y,
         'localStorage.removeItem("token")', dy=0.45)
sr(Y, "SR5"); Y -= 0.65

msg(PX["Login"], PX["User"], Y, "redirectLogin()")
Y -= 0.5

# ═══════════════════════════════════════════════════════════════════════════
# STICKY NOTES — one per requirement
# ═══════════════════════════════════════════════════════════════════════════
note(0.05, 40.0,
     "SR1: JWT issued on login;\nrequired for all protected\nAPI requests (F.R.)",
     width=3.1, height=0.80)

note(0.05, 33.5,
     "SR2: 5 failed attempts\nin 15 min → lock (F.R.)",
     width=3.1, height=0.65)

note(0.05, 28.5,
     "SR3: passwords stored as\nBCrypt hash — never\nplaintext (N.F.R.)",
     width=3.1, height=0.80)

note(0.05, 21.0,
     "SR4: login, logout, failed\nattempts → audit_log (N.F.R.)",
     width=3.1, height=0.65)

note(0.05, 10.5,
     "SR5: JWT revoked + session\ninvalidated immediately\non logout (F.R.)",
     width=3.1, height=0.80)

# ── title ─────────────────────────────────────────────────────────────────
ax.text(FW/2, FH-0.55,
        "SecureHome — 5.4.1 User Authentication & Session Management",
        ha="center", va="center", fontsize=13,
        fontweight="bold", color="#1A5276")

# ── save ─────────────────────────────────────────────────────────────────
out = pathlib.Path(
    r"c:\Users\user\MyProjects\SecureHome\docs\SecureHome_Auth_SequenceDiagram.png")
out.parent.mkdir(parents=True, exist_ok=True)
fig.savefig(out, dpi=150, bbox_inches="tight",
            facecolor=fig.get_facecolor())
print(f"Saved: {out}")
plt.close(fig)
