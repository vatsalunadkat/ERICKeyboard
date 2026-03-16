import { useState } from "react";

const COLORS = [
  { name: "Red",    hex: "#E8281E", text: "#fff" },
  { name: "Orange", hex: "#F5840C", text: "#000" },
  { name: "Yellow", hex: "#F5C518", text: "#000" },
  { name: "Green",  hex: "#2DB84B", text: "#fff" },
  { name: "Blue",   hex: "#1A6FE8", text: "#fff" },
  { name: "Indigo", hex: "#5548C8", text: "#fff" },
  { name: "Violet", hex: "#9B30D0", text: "#fff" },
  { name: "Black",  hex: "#222228", text: "#fff" },
];

const DIRS = ["N","NE","E","SE","S","SW","W","NW"];
const DIR_ANGLE = { N:270, NE:315, E:0, SE:45, S:90, SW:135, W:180, NW:225 };

const L_GROUPS = {
  N:  ["a","b","c","d","e","'"],
  NE: ["f","g","h","i","j","/"],
  E:  ["k","l","m","n","o",";"],
  SE: ["p","q","r","s","t","-"],
  S:  ["u","v","w","x","y","="],
  SW: ["z","\\","[","]","`"," "],
  W:  ["1","2","3","4","5"," "],
  NW: ["6","7","8","9","0"," "],
};

const toRad = d => d * Math.PI / 180;
const polar = (cx, cy, r, deg) => ({
  x: cx + r * Math.cos(toRad(deg)),
  y: cy + r * Math.sin(toRad(deg)),
});

// Arc sector path (annular wedge)
const sectorPath = (cx, cy, r1, r2, a1Deg, a2Deg) => {
  const p = (r, a) => { const pt = polar(cx, cy, r, a); return `${pt.x.toFixed(3)},${pt.y.toFixed(3)}`; };
  const lg = (a2Deg - a1Deg) > 180 ? 1 : 0;
  return `M${p(r2,a1Deg)} A${r2},${r2} 0 ${lg},1 ${p(r2,a2Deg)} L${p(r1,a2Deg)} A${r1},${r1} 0 ${lg},0 ${p(r1,a1Deg)}Z`;
};

// Compute fan tile positions for a given direction
// Row 0 (outer, 3 tiles): letters 0,1,2 — wider spread, further out
// Row 1 (inner, 3 tiles): letters 3,4,5 — tighter spread, closer in
// Each tile is a trapezoid-ish rounded rect rotated to face outward
const JOYSTICK_R = 100;  // SVG units, joystick circle radius
const CX = 160, CY = 160;
const SVG_SIZE = 320;

// Fan config
const ROW0_R = 142;  // center of outer row tiles (from dial center)
const ROW1_R = 115;  // center of inner row tiles
const TILE_W = 34;
const TILE_H = 30;
const ROW0_SPREAD = 15; // degrees between tiles in outer row
const ROW1_SPREAD = 12; // degrees between tiles in inner row

const getTileTransform = (dirAngle, rowIdx, colIdx, numCols) => {
  const spread = rowIdx === 0 ? ROW0_SPREAD : ROW1_SPREAD;
  const totalCols = numCols;
  const offset = (colIdx - (totalCols - 1) / 2) * spread;
  const tileAngle = dirAngle + offset;
  const radius = rowIdx === 0 ? ROW0_R : ROW1_R;
  const { x, y } = polar(CX, CY, radius, tileAngle);
  // Rotate tile to face outward from center
  const rotateDeg = tileAngle + 90; // perpendicular to radial direction feels wrong; use tileAngle itself
  return { x, y, rotateDeg: tileAngle - 90 }; // tile top faces outward
};

// Wedge path for right dial
const wedgePath = (cx, cy, r1, r2, centerDeg, halfSpan) => {
  return sectorPath(cx, cy, r1, r2, centerDeg - halfSpan, centerDeg + halfSpan);
};

export default function ERICKKeyboard() {
  const [lSel, setLSel] = useState(null);
  const [rSel, setRSel] = useState(null);
  const [typed, setTyped] = useState("Hi,");
  const [flashChar, setFlashChar] = useState(null);
  const [animKey, setAnimKey] = useState(0);

  const handleDir = (d) => {
    if (lSel === d) { setLSel(null); setRSel(null); return; }
    setLSel(d); setRSel(null);
    setAnimKey(k => k + 1);
  };

  const handleRight = (i) => {
    if (!lSel) return;
    const c = L_GROUPS[lSel][i];
    if (!c || c === " ") return;
    setRSel(i); setFlashChar(c);
    setTyped(p => p + c);
    setTimeout(() => { setLSel(null); setRSel(null); setFlashChar(null); }, 380);
  };

  const letters = lSel ? L_GROUPS[lSel] : null;

  // Build fan tiles for active direction
  const renderFanTiles = () => {
    if (!lSel) return null;
    const dirAngle = DIR_ANGLE[lSel];
    const grp = L_GROUPS[lSel];
    const validLetters = grp.filter(l => l && l !== " ");
    const count = validLetters.length; // typically 6, sometimes 5

    // Split: outer row = first 3, inner row = remaining
    const outerLetters = grp.slice(0, 3);
    const innerLetters = grp.slice(3, 6);

    const rows = [
      { letters: outerLetters, rowIdx: 0 },
      { letters: innerLetters, rowIdx: 1 },
    ];

    return rows.flatMap(({ letters: rowLetters, rowIdx }) => {
      const validCount = rowLetters.filter(l => l && l !== " ").length;
      return rowLetters.map((letter, colIdx) => {
        if (!letter || letter === " ") return null;
        const globalIdx = rowIdx * 3 + colIdx;
        const c = COLORS[globalIdx];
        const { x, y, rotateDeg } = getTileTransform(dirAngle, rowIdx, colIdx, 3);
        const active = rSel === globalIdx;
        const delay = globalIdx * 0.035;

        return (
          <g key={`tile-${globalIdx}`}
            transform={`translate(${x.toFixed(2)},${y.toFixed(2)}) rotate(${rotateDeg.toFixed(1)})`}
            onClick={(e) => { e.stopPropagation(); handleRight(globalIdx); }}
            style={{
              cursor: "pointer",
              animation: `tileBloom 0.25s cubic-bezier(0.34,1.56,0.64,1) ${delay}s both`,
            }}
          >
            {/* Drop shadow */}
            <rect x={-TILE_W/2 + 1.5} y={-TILE_H/2 + 2.5}
              width={TILE_W} height={TILE_H} rx={6}
              fill="rgba(0,0,0,0.4)" />

            {/* Tile body */}
            <rect x={-TILE_W/2} y={-TILE_H/2}
              width={TILE_W} height={TILE_H} rx={6}
              fill={c.hex}
              stroke={active ? "#fff" : "rgba(255,255,255,0.15)"}
              strokeWidth={active ? 2.5 : 0.5}
            />

            {/* Active glow */}
            {active && (
              <rect x={-TILE_W/2 - 4} y={-TILE_H/2 - 4}
                width={TILE_W + 8} height={TILE_H + 8} rx={9}
                fill="none" stroke={c.hex} strokeWidth="3" opacity="0.5"
              />
            )}

            {/* Letter */}
            <text x={0} y={1}
              textAnchor="middle" dominantBaseline="central"
              fontSize={active ? 15 : 14}
              fontWeight="800"
              fill={c.text}
              style={{ fontFamily: "'JetBrains Mono', monospace", pointerEvents: "none" }}
            >{letter}</text>

            {/* Color position dot at bottom */}
            <circle cx={0} cy={TILE_H/2 - 5} r={2.5}
              fill={active ? "#fff" : "rgba(255,255,255,0.4)"} />
          </g>
        );
      });
    });
  };

  return (
    <div style={{
      minHeight: "100vh",
      background: "#0D0D0D",
      display: "flex",
      flexDirection: "column",
      alignItems: "center",
      justifyContent: "center",
      fontFamily: "'JetBrains Mono', monospace",
      padding: 16,
    }}>
      <style>{`
        @import url('https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;700;800&display=swap');
        * { box-sizing: border-box; user-select: none; -webkit-user-select: none; }
        @keyframes tileBloom {
          from { transform: scale(0.2) rotate(15deg); opacity: 0; }
          to   { transform: scale(1)   rotate(0deg);  opacity: 1; }
        }
        @keyframes blink { 0%,100%{opacity:1} 50%{opacity:0} }
        @keyframes charPop {
          0%   { transform: scale(0.4); opacity: 0; }
          60%  { transform: scale(1.35); }
          100% { transform: scale(1);   opacity: 1; }
        }
        @keyframes slideDown {
          from { transform: translateY(-8px); opacity: 0; }
          to   { transform: translateY(0);    opacity: 1; }
        }
      `}</style>

      {/* ── Phone Shell ── */}
      <div style={{
        width: 410, background: "#111",
        borderRadius: 44,
        border: "1.5px solid #1C1C1C",
        boxShadow: "0 50px 120px rgba(0,0,0,0.98), inset 0 1px 0 rgba(255,255,255,0.04)",
        overflow: "hidden",
      }}>

        {/* Status bar */}
        <div style={{ background: "#0A0A0A", height: 34, display: "flex", alignItems: "center", justifyContent: "space-between", padding: "0 20px" }}>
          <span style={{ color: "#fff", fontSize: 11, fontWeight: 700 }}>9:41</span>
          <div style={{ width: 80, height: 16, background: "#111", borderRadius: 8 }} />
          <span style={{ color: "#fff", fontSize: 10, opacity: 0.6 }}>⬤⬤⬤</span>
        </div>

        {/* Message input */}
        <div style={{ background: "#111", padding: "8px 14px 10px", display: "flex", alignItems: "center", gap: 10, borderBottom: "1px solid #1A1A1A" }}>
          <div style={{ width: 34, height: 34, borderRadius: "50%", background: "#2C2C2E", display: "flex", alignItems: "center", justifyContent: "center", fontSize: 16, flexShrink: 0 }}>😊</div>
          <div style={{ flex: 1, background: "#1C1C1E", borderRadius: 22, padding: "9px 16px", display: "flex", alignItems: "center", gap: 6, border: "1px solid #2A2A2A" }}>
            <span style={{ color: "#fff", fontSize: 16, flex: 1, fontFamily: "'JetBrains Mono', monospace", letterSpacing: 1 }}>{typed}</span>
            {flashChar && <span style={{ color: "#30D158", fontSize: 22, fontWeight: 800, animation: "charPop 0.3s ease", fontFamily: "'JetBrains Mono', monospace" }}>{flashChar}</span>}
            <div style={{ width: 2, height: 18, background: "#30D158", animation: "blink 1s step-end infinite", flexShrink: 0 }} />
          </div>
          <div style={{ width: 34, height: 34, borderRadius: "50%", background: "#30D158", display: "flex", alignItems: "center", justifyContent: "center", flexShrink: 0, fontSize: 16 }}>▶</div>
        </div>

        {/* ── Preview bar ── */}
        <div style={{
          background: "#0D0D0D",
          borderBottom: "1px solid #1A1A1A",
          minHeight: 64,
          padding: "8px 14px",
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
        }}>
          {letters ? (
            <div style={{ display: "flex", alignItems: "center", gap: 5, width: "100%", animation: "slideDown 0.16s ease" }}>
              <div style={{ background: "#1C1C1E", borderRadius: 8, padding: "5px 10px", color: "#8E8E93", fontSize: 10, fontWeight: 700, letterSpacing: 2, flexShrink: 0 }}>{lSel}</div>
              <div style={{ display: "flex", gap: 4, flex: 1, justifyContent: "center" }}>
                {letters.map((l, i) => {
                  if (!l || l === " ") return null;
                  const c = COLORS[i];
                  const active = rSel === i;
                  return (
                    <div key={i} onClick={() => handleRight(i)} style={{
                      width: 42, height: 42, background: c.hex, borderRadius: 10,
                      display: "flex", alignItems: "center", justifyContent: "center",
                      cursor: "pointer", flexShrink: 0,
                      transform: active ? "scale(1.18)" : "scale(1)",
                      boxShadow: active ? `0 0 0 2.5px #fff, 0 0 20px ${c.hex}88` : "0 3px 8px rgba(0,0,0,0.45)",
                      transition: "transform 0.12s, box-shadow 0.12s",
                      animation: `tileBloom 0.2s cubic-bezier(0.34,1.56,0.64,1) ${i * 0.03}s both`,
                    }}>
                      <span style={{ color: c.text, fontSize: 20, fontWeight: 800, fontFamily: "'JetBrains Mono', monospace" }}>{l}</span>
                    </div>
                  );
                })}
              </div>
              <div style={{ minWidth: 44, textAlign: "right", color: rSel !== null ? COLORS[rSel].hex : "#2C2C2E", fontSize: 8, fontWeight: 700, letterSpacing: 1, flexShrink: 0 }}>
                {rSel !== null ? COLORS[rSel].name.toUpperCase() : "PICK\nCOLOR"}
              </div>
            </div>
          ) : (
            <div style={{ display: "flex", gap: 6, alignItems: "center" }}>
              <span style={{ color: "#2A2A2A", fontSize: 9, letterSpacing: 2 }}>PUSH A DIRECTION</span>
              <div style={{ display: "flex", gap: 3 }}>
                {COLORS.map((c, i) => <div key={i} style={{ width: 10, height: 10, borderRadius: 3, background: c.hex, opacity: 0.5 }} />)}
              </div>
            </div>
          )}
        </div>

        {/* ── Keyboard ── */}
        <div style={{ background: "#0A0A0A", padding: "8px 8px 16px", display: "flex", gap: 6, alignItems: "center" }}>

          {/* LEFT DIAL */}
          <div style={{ flex: 1.15 }}>
            <svg
              key={animKey}
              viewBox={`0 0 ${SVG_SIZE} ${SVG_SIZE}`}
              style={{ width: "100%", display: "block", overflow: "visible" }}
            >
              <defs>
                <radialGradient id="dialBg" cx="50%" cy="50%" r="50%">
                  <stop offset="0%" stopColor="#C9C9CE" />
                  <stop offset="100%" stopColor="#AEAEB5" />
                </radialGradient>
                <radialGradient id="hubGrad" cx="38%" cy="32%" r="65%">
                  <stop offset="0%" stopColor="#E0E0E5" />
                  <stop offset="100%" stopColor="#8A8A90" />
                </radialGradient>
                <filter id="tileDropShadow">
                  <feDropShadow dx="0" dy="3" stdDeviation="3" floodColor="#000" floodOpacity="0.5" />
                </filter>
                <filter id="segGlow">
                  <feGaussianBlur stdDeviation="4" result="b" />
                  <feMerge><feMergeNode in="b"/><feMergeNode in="SourceGraphic"/></feMerge>
                </filter>
              </defs>

              {/* Dial plate */}
              <circle cx={CX} cy={CY} r={108} fill="#BCBCC2" />
              <circle cx={CX} cy={CY} r={106} fill="url(#dialBg)" />

              {/* 8 direction hit zones + subtle segment shading */}
              {DIRS.map((d, di) => {
                const angle = DIR_ANGLE[d];
                const half = 22;
                const sel = lSel === d;
                return (
                  <g key={d} onClick={() => handleDir(d)} style={{ cursor: "pointer" }}>
                    {/* Segment fill */}
                    <path
                      d={sectorPath(CX, CY, 40, 104, angle - half, angle + half)}
                      fill={sel ? "rgba(0,0,0,0.1)" : "rgba(0,0,0,0.01)"}
                      style={{ transition: "fill 0.15s" }}
                    />
                    {/* Direction label — only shows when no active selection */}
                    {!lSel && (() => {
                      const { x, y } = polar(CX, CY, 72, angle);
                      return (
                        <text x={x} y={y} textAnchor="middle" dominantBaseline="central"
                          fontSize="9" fontWeight="700"
                          fill="rgba(0,0,0,0.28)"
                          style={{ fontFamily: "'JetBrains Mono', monospace", pointerEvents: "none" }}
                        >{d}</text>
                      );
                    })()}
                    {/* Active segment arc indicator */}
                    {sel && (
                      <path
                        d={sectorPath(CX, CY, 98, 106, angle - half + 1, angle + half - 1)}
                        fill="rgba(255,255,255,0.55)"
                        filter="url(#segGlow)"
                      />
                    )}
                  </g>
                );
              })}

              {/* Divider ticks */}
              {DIRS.map((_, di) => {
                const angle = di * 45 + 270 - 22.5;
                const p1 = polar(CX, CY, 42, angle);
                const p2 = polar(CX, CY, 104, angle);
                return (
                  <line key={di}
                    x1={p1.x} y1={p1.y} x2={p2.x} y2={p2.y}
                    stroke="rgba(0,0,0,0.18)" strokeWidth="1.5"
                  />
                );
              })}

              {/* Hub */}
              <circle cx={CX} cy={CY} r={40} fill="rgba(0,0,0,0.15)" />
              <circle cx={CX} cy={CY} r={38} fill="url(#hubGrad)" />
              <circle cx={CX} cy={CY} r={22} fill="rgba(0,0,0,0.12)" />
              <circle cx={CX} cy={CY} r={20} fill="#7A7A80" />

              {lSel ? (
                <text x={CX} y={CY} textAnchor="middle" dominantBaseline="central"
                  fontSize="16" fontWeight="800" fill="#fff"
                  style={{ fontFamily: "'JetBrains Mono', monospace", pointerEvents: "none" }}
                >{lSel}</text>
              ) : (
                <text x={CX} y={CY} textAnchor="middle" dominantBaseline="central"
                  fontSize="9" fontWeight="700" fill="rgba(255,255,255,0.45)"
                  style={{ fontFamily: "'JetBrains Mono', monospace", pointerEvents: "none" }}
                >PUSH</text>
              )}

              {/* ── Fan tiles bloom out from active segment ── */}
              <g filter="url(#tileDropShadow)">
                {renderFanTiles()}
              </g>
            </svg>
          </div>

          {/* RIGHT DIAL */}
          <div style={{ flex: 0.9 }}>
            <svg viewBox={`0 0 ${SVG_SIZE} ${SVG_SIZE}`} style={{ width: "100%", display: "block" }}>
              <defs>
                <radialGradient id="rDialBg" cx="50%" cy="50%" r="50%">
                  <stop offset="0%" stopColor="#1E1E1E" />
                  <stop offset="100%" stopColor="#131313" />
                </radialGradient>
              </defs>

              <circle cx={CX} cy={CY} r={108} fill="#0A0A0A" stroke="#1A1A1A" strokeWidth="1" />
              <circle cx={CX} cy={CY} r={106} fill="url(#rDialBg)" />

              {DIRS.map((d, di) => {
                const angle = DIR_ANGLE[d];
                const c = COLORS[di];
                const sel = rSel === di;
                const enabled = !!lSel;
                return (
                  <g key={d} onClick={() => handleRight(di)} style={{ cursor: enabled ? "pointer" : "default" }}>
                    {sel && (
                      <path d={wedgePath(CX, CY, 38, 110, angle, 22)}
                        fill={c.hex} opacity="0.18" />
                    )}
                    <path
                      d={wedgePath(CX, CY, 40, 104, angle, 21)}
                      fill={c.hex}
                      opacity={!enabled ? 0.28 : sel ? 1 : 0.75}
                      stroke={sel ? "#fff" : "rgba(0,0,0,0.3)"}
                      strokeWidth={sel ? 2.5 : 1}
                      style={{ transition: "opacity 0.18s" }}
                    />
                    {(() => {
                      const { x, y } = polar(CX, CY, 72, angle);
                      return (
                        <text x={x} y={y} textAnchor="middle" dominantBaseline="central"
                          fontSize="15" fontWeight="900"
                          fill={c.text} opacity={!enabled ? 0.35 : 1}
                          style={{ fontFamily: "'JetBrains Mono', monospace", pointerEvents: "none" }}
                        >{di + 1}</text>
                      );
                    })()}
                  </g>
                );
              })}

              {/* Dividers */}
              {DIRS.map((_, di) => {
                const angle = di * 45 + 270 - 22.5;
                const p1 = polar(CX, CY, 40, angle);
                const p2 = polar(CX, CY, 104, angle);
                return <line key={di} x1={p1.x} y1={p1.y} x2={p2.x} y2={p2.y} stroke="rgba(0,0,0,0.5)" strokeWidth="2" />;
              })}

              {/* Hub */}
              <circle cx={CX} cy={CY} r={40} fill="#0A0A0A" stroke="#222" strokeWidth="1.5" />
              {rSel !== null ? (
                <>
                  <circle cx={CX} cy={CY} r={30} fill={COLORS[rSel].hex} />
                  <text x={CX} y={CY} textAnchor="middle" dominantBaseline="central"
                    fontSize="16" fontWeight="900" fill={COLORS[rSel].text}
                    style={{ fontFamily: "'JetBrains Mono', monospace" }}
                  >{rSel + 1}</text>
                </>
              ) : (
                <>
                  <circle cx={CX} cy={CY} r={30} fill="#161616" />
                  <text x={CX} y={CY - 6} textAnchor="middle" dominantBaseline="central"
                    fontSize="8" fontWeight="600" fill="#2C2C2E"
                    style={{ fontFamily: "'JetBrains Mono', monospace" }}
                  >CLR</text>
                  <text x={CX} y={CY + 6} textAnchor="middle" dominantBaseline="central"
                    fontSize="8" fontWeight="600" fill="#2C2C2E"
                    style={{ fontFamily: "'JetBrains Mono', monospace" }}
                  >RIGHT</text>
                </>
              )}
            </svg>
          </div>
        </div>

        {/* Status hint */}
        <div style={{ background: "#0A0A0A", padding: "0 14px 16px", textAlign: "center", color: "#2A2A2A", fontSize: 9, letterSpacing: 2, textTransform: "uppercase" }}>
          {!lSel
            ? "Push a direction → then pick a color"
            : rSel !== null
            ? `✓  ${lSel} + ${DIRS[rSel]}  →  "${L_GROUPS[lSel][rSel] || "?"}"`
            : `"${lSel}" active — push a color →`
          }
        </div>
      </div>
    </div>
  );
}