import { useState } from "react";

/* -- Colours (8, mapped to right-dial directions) -- */
const COLORS = [
    { name: "Red",    hex: "#E8281E", text: "#fff" },
    { name: "Orange", hex: "#F5840C", text: "#fff" },
    { name: "Yellow", hex: "#F5C518", text: "#fff" },
    { name: "Green",  hex: "#2DB84B", text: "#fff" },
    { name: "Blue",   hex: "#1A6FE8", text: "#fff" },
    { name: "Indigo", hex: "#2E2080", text: "#fff" },
    { name: "Violet", hex: "#9B30D0", text: "#fff" },
    { name: "Black",  hex: "#111116", text: "#fff" },
];

/* -- Directions -- */
const DIRS = ["N", "NE", "E", "SE", "S", "SW", "W", "NW"];
const DIR_ANGLE = { N: 270, NE: 315, E: 0, SE: 45, S: 90, SW: 135, W: 180, NW: 225 };

/* -- Letter groups: 8 slots per direction (one per colour)
   0=Red 1=Orange 2=Yellow 3=Green 4=Blue 5=Indigo 6=Violet 7=Black
   Ring 1 (outer, 3): 0,1,2
   Ring 2 (mid, 3):   3,4,5
   Ring 3 (inner, 2): 6,7
   null = unassigned -- */
const L_GROUPS = {
    N:  ["a","b","c","d","e",null,null,"'"],
    NE: ["f","g","h","i","j",null,null,"/"],
    E:  ["k","l","m","n","o",null,null,";"],
    SE: ["p","q","r","s","t",null,null,"-"],
    S:  ["u","v","w","x","y",null,null,"="],
    SW: ["z","\\","[","]","`",null,null,null],
    W:  ["1","2","3","4","5",null,null,null],
    NW: ["6","7","8","9","0",null,null,null],
};

/* -- Geometry -- */
const toRad = (d) => (d * Math.PI) / 180;
const polar = (cx, cy, r, deg) => ({
    x: cx + r * Math.cos(toRad(deg)),
    y: cy + r * Math.sin(toRad(deg)),
});

const arcPath = (cx, cy, r1, r2, a1, a2) => {
    const p = (r, a) => {
        const pt = polar(cx, cy, r, a);
        return `${pt.x.toFixed(3)},${pt.y.toFixed(3)}`;
    };
    const lg = (a2 - a1) > 180 ? 1 : 0;
    return `M${p(r2,a1)} A${r2},${r2} 0 ${lg},1 ${p(r2,a2)} L${p(r1,a2)} A${r1},${r1} 0 ${lg},0 ${p(r1,a1)}Z`;
};

/* -- SVG layout -- */
const CX = 200, CY = 200, SVG_SIZE = 400;

/* Left dial rings (radii) */
const HUB_R   = 42;
const R3_IN   = 50;
const R3_OUT  = 73;
const R2_IN   = 76;
const R2_OUT  = 110;
const R1_IN   = 113;
const R1_OUT  = 152;
const BORDER_R = 160;

/* Right dial */
const R_DIAL_R = 140;
const R_HUB    = 42;

/* Angular gap between tiles within a segment */
const TILE_GAP = 0.6;
/* Segment width */
const SEG_W = 45;

/* ====================================================================== */
export default function ERICKKeyboard() {
    const [lSel, setLSel] = useState(null);
    const [rSel, setRSel] = useState(null);
    const [typed, setTyped] = useState("");
    const [flashChar, setFlashChar] = useState(null);

    const typeChar = (ch) => {
        setFlashChar(ch);
        setTyped((p) => p + ch);
        setTimeout(() => { setLSel(null); setRSel(null); setFlashChar(null); }, 380);
    };

    const handleDir = (d) => {
        if (lSel === d) { setLSel(null); return; }
        setLSel(d);
        if (rSel !== null) {
            const ch = L_GROUPS[d]?.[rSel];
            if (ch) { typeChar(ch); return; }
        }
    };

    const handleRight = (i) => {
        if (rSel === i) { setRSel(null); return; }
        setRSel(i);
        if (lSel) {
            const ch = L_GROUPS[lSel]?.[i];
            if (ch) { typeChar(ch); return; }
        }
    };

    const previewLetters = lSel ? L_GROUPS[lSel] : null;

    /* -- Build left dial arc wedge tiles -- */
    const buildLeftTiles = () => {
        const els = [];

        DIRS.forEach((d) => {
            const center = DIR_ANGLE[d];
            const segStart = center - SEG_W / 2;
            const isSel = lSel === d;

            const ring = (rIn, rOut, colorIndices, count) => {
                const totalGap = (count - 1) * TILE_GAP;
                const tileW = (SEG_W - totalGap - 1) / count;
                const startOffset = 0.5;

                colorIndices.forEach((ci, idx) => {
                    const a1 = segStart + startOffset + idx * (tileW + TILE_GAP);
                    const a2 = a1 + tileW;
                    const c = COLORS[ci];
                    const letter = L_GROUPS[d][ci];
                    const active = isSel && rSel === ci;

                    const midAngle = (a1 + a2) / 2;
                    const midR = (rIn + rOut) / 2;
                    const { x: tx, y: ty } = polar(CX, CY, midR, midAngle);

                    els.push(
                        <g key={`${d}-${ci}`}
                           onClick={(e) => { e.stopPropagation(); handleDir(d); handleRight(ci); }}
                           style={{ cursor: "pointer" }}
                        >
                            <path
                                d={arcPath(CX, CY, rIn, rOut, a1, a2)}
                                fill={c.hex}
                                stroke={active ? "#fff" : "rgba(0,0,0,0.4)"}
                                strokeWidth={active ? 2.5 : 0.5}
                                opacity={isSel ? 1 : 0.92}
                            />
                            <text
                                x={tx} y={ty}
                                textAnchor="middle" dominantBaseline="central"
                                fontSize={letter ? "14" : "7"}
                                fontWeight="800"
                                fill={c.text}
                                style={{ fontFamily: "'JetBrains Mono', monospace", pointerEvents: "none" }}
                            >
                                {letter || "\u25CF"}
                            </text>
                        </g>
                    );
                });
            };

            ring(R1_IN, R1_OUT, [0, 1, 2], 3);
            ring(R2_IN, R2_OUT, [3, 4, 5], 3);
            ring(R3_IN, R3_OUT, [6, 7],    2);
        });

        return els;
    };

    /* -- Build right dial -- */
    const buildRightDial = () => {
        const els = [];
        DIRS.forEach((d, di) => {
            const angle = DIR_ANGLE[d];
            const half = 22.5;
            const c = COLORS[di];
            const sel = rSel === di;

            els.push(
                <g key={d} onClick={() => handleRight(di)} style={{ cursor: "pointer" }}>
                    <path
                        d={arcPath(CX, CY, R_HUB + 2, R_DIAL_R, angle - half, angle + half)}
                        fill={c.hex}
                        opacity={sel ? 1 : 0.85}
                        stroke={sel ? "#fff" : "rgba(0,0,0,0.3)"}
                        strokeWidth={sel ? 2.5 : 0.5}
                        style={{ transition: "opacity 0.15s" }}
                    />
                </g>
            );
        });
        return els;
    };

    /* ====================================================================== */
    return (
        <div style={{
            minHeight: "100vh", background: "#000",
            display: "flex", flexDirection: "column",
            alignItems: "center", justifyContent: "center",
            fontFamily: "'JetBrains Mono', monospace", padding: 16,
        }}>
            <style>{`
                @import url('https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;700;800&display=swap');
                * { box-sizing: border-box; user-select: none; -webkit-user-select: none; }
                @keyframes blink  { 0%,100%{opacity:1} 50%{opacity:0} }
                @keyframes charPop { 0%{transform:scale(.4);opacity:0} 60%{transform:scale(1.35)} 100%{transform:scale(1);opacity:1} }
            `}</style>

            {/* -- Container -- */}
            <div style={{ width: 640, background: "#000", overflow: "hidden" }}>

                {/* -- Preview bar -- */}
                <div style={{
                    background: "#000", padding: "10px 14px",
                    display: "flex", alignItems: "center", gap: 8,
                    borderBottom: "2px solid #333",
                }}>
                    <div style={{ display: "flex", gap: 5, flex: 1, justifyContent: "center" }}>
                        {COLORS.map((c, i) => {
                            const letter = previewLetters ? previewLetters[i] : null;
                            const active = rSel === i;
                            return (
                                <div key={i} onClick={() => handleRight(i)} style={{
                                    width: 48, height: 48, background: c.hex, borderRadius: 6,
                                    display: "flex", alignItems: "center", justifyContent: "center",
                                    cursor: "pointer",
                                    border: active ? "2.5px solid #fff" : "2px solid rgba(0,0,0,0.3)",
                                    boxShadow: active ? `0 0 12px ${c.hex}` : "none",
                                }}>
                                    <span style={{
                                        color: c.text, fontSize: letter ? 22 : 16, fontWeight: 800,
                                    }}>
                                        {previewLetters
                                            ? (letter || "\u25CF")
                                            : "\u25CF"
                                        }
                                    </span>
                                </div>
                            );
                        })}
                    </div>
                    {/* Gear icon */}
                    <div style={{
                        width: 40, height: 40, display: "flex", alignItems: "center",
                        justifyContent: "center", fontSize: 22, color: "#666", cursor: "pointer",
                    }}>{"\u2699"}</div>
                </div>

                {/* -- Text input -- */}
                <div style={{
                    background: "#000", padding: "8px 14px",
                    display: "flex", alignItems: "center", gap: 10,
                    borderBottom: "1px solid #222",
                }}>
                    <div style={{
                        flex: 1, background: "#111", borderRadius: 8,
                        padding: "8px 14px", display: "flex", alignItems: "center",
                        gap: 6, border: "1px solid #333", minHeight: 36,
                    }}>
                        <span style={{ color: "#fff", fontSize: 16, flex: 1, letterSpacing: 1 }}>
                            {typed || "\u00A0"}
                        </span>
                        {flashChar && (
                            <span style={{
                                color: "#30D158", fontSize: 22, fontWeight: 800,
                                animation: "charPop 0.3s ease",
                            }}>{flashChar}</span>
                        )}
                        <div style={{
                            width: 2, height: 18, background: "#30D158",
                            animation: "blink 1s step-end infinite",
                        }} />
                    </div>
                </div>

                {/* -- Two dials side by side -- */}
                <div style={{
                    background: "#000", padding: "10px 6px 14px",
                    display: "flex", gap: 8, alignItems: "center",
                }}>
                    {/* --- LEFT DIAL --- */}
                    <div style={{ flex: 1.15 }}>
                        <svg viewBox={`0 0 ${SVG_SIZE} ${SVG_SIZE}`}
                             style={{ width: "100%", display: "block", overflow: "visible" }}>
                            <defs>
                                <radialGradient id="hubGrad" cx="38%" cy="32%" r="65%">
                                    <stop offset="0%" stopColor="#D8D8DD" />
                                    <stop offset="50%" stopColor="#B0B0B5" />
                                    <stop offset="100%" stopColor="#808085" />
                                </radialGradient>
                            </defs>

                            {/* Orange outer border ring */}
                            <circle cx={CX} cy={CY} r={BORDER_R} fill="#F5A623"
                                    stroke="#D4891A" strokeWidth="2" />

                            {/* Black background for the dial area */}
                            <circle cx={CX} cy={CY} r={BORDER_R - 5} fill="#000" />

                            {/* All the arc-sector tiles */}
                            {buildLeftTiles()}

                            {/* White segment divider lines */}
                            {DIRS.map((_, di) => {
                                const angle = di * 45 + 270 - 22.5;
                                const p1 = polar(CX, CY, HUB_R + 1, angle);
                                const p2 = polar(CX, CY, R1_OUT + 1, angle);
                                return (
                                    <line key={di}
                                        x1={p1.x} y1={p1.y} x2={p2.x} y2={p2.y}
                                        stroke="rgba(255,255,255,0.5)" strokeWidth="2"
                                    />
                                );
                            })}

                            {/* Hub -- metallic */}
                            <circle cx={CX} cy={CY} r={HUB_R + 2} fill="#333" />
                            <circle cx={CX} cy={CY} r={HUB_R} fill="url(#hubGrad)"
                                    stroke="#888" strokeWidth="1" />
                            <circle cx={CX} cy={CY} r={HUB_R - 14} fill="rgba(0,0,0,0.06)" />
                            <circle cx={CX} cy={CY} r={HUB_R - 16} fill="#A0A0A5" />
                            <circle cx={CX} cy={CY} r={HUB_R - 25} fill="rgba(0,0,0,0.08)" />
                            <circle cx={CX} cy={CY} r={HUB_R - 27} fill="#8A8A8F" />
                        </svg>
                    </div>

                    {/* --- RIGHT DIAL --- */}
                    <div style={{ flex: 0.9 }}>
                        <svg viewBox={`0 0 ${SVG_SIZE} ${SVG_SIZE}`}
                             style={{ width: "100%", display: "block" }}>
                            <defs>
                                <radialGradient id="rHubGrad" cx="38%" cy="32%" r="65%">
                                    <stop offset="0%" stopColor="#D0D0D5" />
                                    <stop offset="50%" stopColor="#A8A8AD" />
                                    <stop offset="100%" stopColor="#7A7A80" />
                                </radialGradient>
                            </defs>

                            {/* 8 solid colour wedges */}
                            {buildRightDial()}

                            {/* Hub */}
                            <circle cx={CX} cy={CY} r={R_HUB + 2} fill="#222" />
                            <circle cx={CX} cy={CY} r={R_HUB}
                                    fill={rSel !== null ? COLORS[rSel].hex : "url(#rHubGrad)"}
                                    stroke="#555" strokeWidth="1" />
                            <circle cx={CX} cy={CY} r={R_HUB - 14}
                                    fill={rSel !== null ? COLORS[rSel].hex : "#9A9AA0"} />
                        </svg>
                    </div>
                </div>
            </div>
        </div>
    );
}
