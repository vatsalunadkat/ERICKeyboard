import SwiftUI

enum WheelDirection: CaseIterable {
    case none
    case n
    case ne
    case e
    case se
    case s
    case sw
    case w
    case nw

    static let orderedDirections: [WheelDirection] = [.n, .ne, .e, .se, .s, .sw, .w, .nw]

    var centerAngleDegrees: Double {
        switch self {
        case .n: return -90
        case .ne: return -45
        case .e: return 0
        case .se: return 45
        case .s: return 90
        case .sw: return 135
        case .w: return 180
        case .nw: return 225
        case .none: return 0
        }
    }
}

enum WheelMode {
    case normal
    case shifted
    case capsLocked

    var usesShiftedSymbols: Bool {
        self != .normal
    }
}

private struct LeftWheelSection {
    let direction: WheelDirection
    let outer: [String]
    let middle: [String]
    let inner: [String]
}

private struct RightWheelAction {
    let title: String
    let systemImage: String?
}

struct JoystickView: View {
    var isRightSide: Bool
    var activeDirection: WheelDirection
    var keyboardMode: WheelMode
    var isEfficiency: Bool = false
    var colorPaletteKey: String = "default"
    var onTouch: ((Float, Float, Bool, Bool) -> Void)?

    @State private var thumbOffset: CGSize = .zero

    var body: some View {
        GeometryReader { geometry in
            let diameter = min(geometry.size.width, geometry.size.height) * 0.98
            let thumbDiameter = diameter * (isRightSide ? 0.24 : 0.26)
            let thumbRadius = thumbDiameter / 2
            let maxThumbDistance = (diameter / 2) - thumbRadius - (diameter * 0.035)

            ZStack {
                if isRightSide {
                    RightWheelBackground(activeDirection: activeDirection, keyboardMode: keyboardMode, colorPaletteKey: colorPaletteKey)
                } else {
                    LeftWheelBackground(activeDirection: activeDirection, keyboardMode: keyboardMode, isEfficiency: isEfficiency, colorPaletteKey: colorPaletteKey)
                }

                ZStack {
                    Circle()
                        .fill(Color(hex: "#BDBDBD"))
                    Circle()
                        .fill(Color(hex: "#8E8E8E"))
                        .padding(thumbDiameter * 0.24)
                }
                .frame(width: thumbDiameter, height: thumbDiameter)
                .offset(thumbOffset)
                .shadow(color: .black.opacity(0.18), radius: 5, y: 2)
            }
            .frame(width: diameter, height: diameter)
            .contentShape(Circle())
            .gesture(
                DragGesture(minimumDistance: 0)
                    .onChanged { value in
                        let center = diameter / 2
                        let rawDx = value.location.x - center
                        let rawDy = value.location.y - center
                        let distance = sqrt((rawDx * rawDx) + (rawDy * rawDy))

                        var clampedDx = rawDx
                        var clampedDy = rawDy

                        if distance > maxThumbDistance {
                            let ratio = maxThumbDistance / distance
                            clampedDx *= ratio
                            clampedDy *= ratio
                        }

                        thumbOffset = CGSize(width: clampedDx, height: clampedDy)
                        onTouch?(Float(clampedDx), Float(clampedDy), true, false)
                    }
                    .onEnded { _ in
                        withAnimation(.spring(response: 0.22, dampingFraction: 0.68)) {
                            thumbOffset = .zero
                        }
                        onTouch?(0, 0, false, true)
                    }
            )
            .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
    }
}

private struct LeftWheelBackground: View {
    let activeDirection: WheelDirection
    let keyboardMode: WheelMode
    var isEfficiency: Bool = false
    var colorPaletteKey: String = "default"

    private var outerColors: [String] {
        let p = ColorPaletteDefinitions.palette(for: colorPaletteKey)
        return [p[0].hex, p[1].hex, p[2].hex]
    }
    private var middleColors: [String] {
        let p = ColorPaletteDefinitions.palette(for: colorPaletteKey)
        return [p[3].hex, p[4].hex, p[5].hex]
    }
    private var innerColors: [String] {
        let p = ColorPaletteDefinitions.palette(for: colorPaletteKey)
        return [p[6].hex, p[7].hex]
    }

    var body: some View {
        GeometryReader { geometry in
            let size = min(geometry.size.width, geometry.size.height)
            let wheelSections = leftWheelSections(for: keyboardMode, efficiency: isEfficiency)
            let hideBlackRingGaps = activeDirection != .none
            let sectorGap: Double = 1.2
            let cellGap: Double = 0.75
            let outerInnerRatio: CGFloat = hideBlackRingGaps ? 0.67 : 0.70
            let middleInnerRatio: CGFloat = hideBlackRingGaps ? 0.45 : 0.48
            let middleOuterRatio: CGFloat = hideBlackRingGaps ? 0.665 : 0.69
            let innerInnerRatio: CGFloat = 0.16
            let innerOuterRatio: CGFloat = hideBlackRingGaps ? 0.445 : 0.47

            ZStack {
                Circle()
                    .fill(Color.white)

                ForEach(wheelSections, id: \.direction) { section in
                    let sectorStart = section.direction.centerAngleDegrees - 22.5 + sectorGap
                    let sectorEnd = section.direction.centerAngleDegrees + 22.5 - sectorGap
                    let isSelected = activeDirection == section.direction
                    let dimmed = activeDirection != .none && !isSelected

                    SectorSlice(
                        startAngle: sectorStart,
                        endAngle: sectorEnd,
                        innerRadiusRatio: 0.12,
                        outerRadiusRatio: 0.985
                    )
                    .fill(Color.black.opacity(isSelected ? 0.96 : 0.92))
                    .opacity(dimmed ? 0.55 : 1)

                    sectorRow(
                        items: section.outer,
                        colors: outerColors.map(Color.init(hex:)),
                        colorHexes: outerColors,
                        startAngle: sectorStart,
                        endAngle: sectorEnd,
                        innerRatio: outerInnerRatio,
                        outerRatio: 0.975,
                        size: size,
                        baseFontSize: size * 0.085,
                        angleGap: cellGap,
                        opacity: dimmed ? 0.55 : 1
                    )

                    sectorRow(
                        items: section.middle,
                        colors: middleColors.map(Color.init(hex:)),
                        colorHexes: middleColors,
                        startAngle: sectorStart,
                        endAngle: sectorEnd,
                        innerRatio: middleInnerRatio,
                        outerRatio: middleOuterRatio,
                        size: size,
                        baseFontSize: size * 0.068,
                        angleGap: cellGap,
                        opacity: dimmed ? 0.55 : 1
                    )

                    if !section.inner.isEmpty {
                        sectorRow(
                            items: section.inner,
                            colors: innerColors.map(Color.init(hex:)),
                            colorHexes: innerColors,
                            startAngle: sectorStart,
                            endAngle: sectorEnd,
                            innerRatio: innerInnerRatio,
                            outerRatio: innerOuterRatio,
                            size: size,
                            baseFontSize: size * 0.053,
                            angleGap: cellGap,
                            opacity: dimmed ? 0.55 : 1
                        )
                    }

                    if isSelected {
                        SectorSlice(
                            startAngle: section.direction.centerAngleDegrees - 22.5,
                            endAngle: section.direction.centerAngleDegrees + 22.5,
                            innerRadiusRatio: 0.12,
                            outerRadiusRatio: 0.985
                        )
                        .stroke(Color.white.opacity(0.85), lineWidth: size * 0.018)
                    }
                }

                Circle()
                    .stroke(Color.white, lineWidth: size * 0.024)

                Circle()
                    .fill(Color.black.opacity(0.92))
                    .frame(width: size * 0.23, height: size * 0.23)
            }
            .frame(width: size, height: size)
        }
    }

    @ViewBuilder
    private func sectorRow(
        items: [String],
        colors: [Color],
        colorHexes: [String] = [],
        startAngle: Double,
        endAngle: Double,
        innerRatio: CGFloat,
        outerRatio: CGFloat,
        size: CGFloat,
        baseFontSize: CGFloat,
        angleGap: Double,
        opacity: Double
    ) -> some View {
        ForEach(Array(items.enumerated()), id: \.offset) { index, item in
            let span = (endAngle - startAngle) / Double(items.count)
            let cellStart = startAngle + (Double(index) * span) + angleGap
            let cellEnd = startAngle + (Double(index + 1) * span) - angleGap
            let angle = (cellStart + cellEnd) / 2
            let radiusRatio = (innerRatio + outerRatio) / 2
            let labelPoint = point(in: size, radiusRatio: radiusRatio, angleDegrees: angle)
            let metrics = sectorLabelMetrics(
                size: size,
                item: item,
                innerRatio: innerRatio,
                outerRatio: outerRatio,
                cellStart: cellStart,
                cellEnd: cellEnd,
                baseFontSize: baseFontSize
            )

            SectorSlice(
                startAngle: cellStart,
                endAngle: cellEnd,
                innerRadiusRatio: innerRatio,
                outerRadiusRatio: outerRatio
            )
            .fill(colors[min(index, colors.count - 1)])
            .opacity(opacity)

            if !item.isEmpty {
                let textColor: Color = {
                    if index < colorHexes.count {
                        return ColorPaletteDefinitions.contrastTextColor(hex: colorHexes[index])
                    }
                    return .white
                }()
                Text(item)
                    .font(.system(size: metrics.fontSize, weight: .bold, design: .rounded))
                    .foregroundStyle(textColor)
                    .shadow(color: .black.opacity(0.25), radius: 1, y: 1)
                    .minimumScaleFactor(0.45)
                    .lineLimit(1)
                    .frame(width: metrics.width, height: metrics.height)
                    .position(labelPoint)
                    .opacity(opacity)
            }
        }
    }

    private func leftWheelSections(for mode: WheelMode, efficiency: Bool = false) -> [LeftWheelSection] {
        if efficiency {
            if mode.usesShiftedSymbols {
                return [
                    LeftWheelSection(direction: .n,  outer: ["T", "S", "G"], middle: ["&", "+", ""],  inner: ["K", "$"]),
                    LeftWheelSection(direction: .ne, outer: ["I", "A", "N"], middle: ["P", "?", ""],  inner: ["\"", ""]),
                    LeftWheelSection(direction: .e,  outer: ["V", "L", "E"], middle: ["R", "X", ""],  inner: [":", ""]),
                    LeftWheelSection(direction: .se, outer: ["_", "Y", "D"], middle: ["O", "M", ""],  inner: ["", ""]),
                    LeftWheelSection(direction: .s,  outer: ["~", "^", "B"], middle: ["F", "U", ""],  inner: ["", ""]),
                    LeftWheelSection(direction: .sw, outer: ["|", "{", "}"], middle: ["%", "Q", "J"], inner: ["", ""]),
                    LeftWheelSection(direction: .w,  outer: ["", "", ""],   middle: ["", "", "@"],   inner: ["Z", "#"]),
                    LeftWheelSection(direction: .nw, outer: ["H", "W", "!"], middle: ["*", "(", ""],  inner: ["C", ")"])
                ]
            }
            return [
                LeftWheelSection(direction: .n,  outer: ["t", "s", "g"], middle: ["7", "=", ""],  inner: ["k", "4"]),
                LeftWheelSection(direction: .ne, outer: ["i", "a", "n"], middle: ["p", "/", ""],  inner: ["'", ""]),
                LeftWheelSection(direction: .e,  outer: ["v", "l", "e"], middle: ["r", "x", ""],  inner: [";", ""]),
                LeftWheelSection(direction: .se, outer: ["-", "y", "d"], middle: ["o", "m", ""],  inner: ["", ""]),
                LeftWheelSection(direction: .s,  outer: ["`", "6", "b"], middle: ["f", "u", ""],  inner: ["", ""]),
                LeftWheelSection(direction: .sw, outer: ["\\", "[", "]"], middle: ["5", "q", "j"], inner: ["", ""]),
                LeftWheelSection(direction: .w,  outer: ["", "", ""],   middle: ["", "", "2"],   inner: ["z", "3"]),
                LeftWheelSection(direction: .nw, outer: ["h", "w", "1"], middle: ["8", "9", ""],  inner: ["c", "0"])
            ]
        }

        if mode.usesShiftedSymbols {
            return [
                LeftWheelSection(direction: .n, outer: ["A", "B", "C"], middle: ["D", "E", ""], inner: ["\"", ""]),
                LeftWheelSection(direction: .ne, outer: ["F", "G", "H"], middle: ["I", "J", ""], inner: ["?", ""]),
                LeftWheelSection(direction: .e, outer: ["K", "L", "M"], middle: ["N", "O", ""], inner: [":", ""]),
                LeftWheelSection(direction: .se, outer: ["P", "Q", "R"], middle: ["S", "T", ""], inner: ["_", ""]),
                LeftWheelSection(direction: .s, outer: ["U", "V", "W"], middle: ["X", "Y", ""], inner: ["+", ""]),
                LeftWheelSection(direction: .sw, outer: ["Z", "|", "{"], middle: ["}", "~", ""], inner: ["", ""]),
                LeftWheelSection(direction: .w, outer: ["!", "@", "#"], middle: ["$", "%", ""], inner: ["", ""]),
                LeftWheelSection(direction: .nw, outer: ["^", "&", "*"], middle: ["(", ")", ""], inner: ["", ""])
            ]
        }

        return [
            LeftWheelSection(direction: .n, outer: ["a", "b", "c"], middle: ["d", "e", ""], inner: ["'", ""]),
            LeftWheelSection(direction: .ne, outer: ["f", "g", "h"], middle: ["i", "j", ""], inner: ["/", ""]),
            LeftWheelSection(direction: .e, outer: ["k", "l", "m"], middle: ["n", "o", ""], inner: [";", ""]),
            LeftWheelSection(direction: .se, outer: ["p", "q", "r"], middle: ["s", "t", ""], inner: ["-", ""]),
            LeftWheelSection(direction: .s, outer: ["u", "v", "w"], middle: ["x", "y", ""], inner: ["=", ""]),
            LeftWheelSection(direction: .sw, outer: ["z", "\\", "["], middle: ["]", "`", ""], inner: ["", ""]),
            LeftWheelSection(direction: .w, outer: ["1", "2", "3"], middle: ["4", "5", ""], inner: ["", ""]),
            LeftWheelSection(direction: .nw, outer: ["6", "7", "8"], middle: ["9", "0", ""], inner: ["", ""])
        ]
    }
}

private struct SectorLabelMetrics {
    let width: CGFloat
    let height: CGFloat
    let fontSize: CGFloat
}

private func sectorLabelMetrics(
    size: CGFloat,
    item: String,
    innerRatio: CGFloat,
    outerRatio: CGFloat,
    cellStart: Double,
    cellEnd: Double,
    baseFontSize: CGFloat
) -> SectorLabelMetrics {
    let halfSize = size / 2
    let innerRadius = halfSize * innerRatio
    let outerRadius = halfSize * outerRatio
    let midRadius = (innerRadius + outerRadius) / 2
    let angleSpanRadians = CGFloat(abs(cellEnd - cellStart) * Double.pi / 180.0)
    let arcWidth = max(midRadius * angleSpanRadians * 0.84, size * 0.055)
    let radialHeight = max((outerRadius - innerRadius) * 0.8, size * 0.055)
    let widthFontLimit = arcWidth / max(CGFloat(item.count) * 0.62, 0.8)
    let heightFontLimit = radialHeight * 0.76
    let fontSize = max(min(baseFontSize, widthFontLimit, heightFontLimit), size * 0.038)

    return SectorLabelMetrics(
        width: arcWidth,
        height: radialHeight,
        fontSize: fontSize
    )
}

private struct RightWheelBackground: View {
    let activeDirection: WheelDirection
    let keyboardMode: WheelMode
    var colorPaletteKey: String = "default"

    private var palette: [ColorPaletteEntry] {
        ColorPaletteDefinitions.palette(for: colorPaletteKey)
    }

    private static let directionOrder: [WheelDirection] = [.n, .ne, .e, .se, .s, .sw, .w, .nw]

    private var sectorColors: [WheelDirection: Color] {
        var map: [WheelDirection: Color] = [:]
        for (i, dir) in Self.directionOrder.enumerated() {
            if i < palette.count {
                map[dir] = Color(hex: palette[i].hex)
            }
        }
        return map
    }

    private var sectorHexes: [WheelDirection: String] {
        var map: [WheelDirection: String] = [:]
        for (i, dir) in Self.directionOrder.enumerated() {
            if i < palette.count {
                map[dir] = palette[i].hex
            }
        }
        return map
    }

    var body: some View {
        GeometryReader { geometry in
            let size = min(geometry.size.width, geometry.size.height)
            let sectorGap: Double = 1.4

            ZStack {
                Circle()
                    .fill(Color.white)

                ForEach(WheelDirection.orderedDirections, id: \.self) { direction in
                    let action = action(for: direction, mode: keyboardMode)
                    let selected = activeDirection == direction
                    let dimmed = activeDirection != .none && !selected
                    let labelPoint = point(in: size, radiusRatio: 0.67, angleDegrees: direction.centerAngleDegrees)

                    SectorSlice(
                        startAngle: direction.centerAngleDegrees - 22.5 + sectorGap,
                        endAngle: direction.centerAngleDegrees + 22.5 - sectorGap,
                        innerRadiusRatio: 0.14,
                        outerRadiusRatio: 0.985
                    )
                    .fill(sectorColors[direction] ?? .gray)
                    .brightness(selected ? 0.08 : 0)
                    .opacity(dimmed ? 0.55 : 1)

                    let contrastColor = ColorPaletteDefinitions.contrastTextColor(hex: sectorHexes[direction] ?? "#000000")
                    RightActionLabel(action: action, size: size * 0.17, textColor: contrastColor)
                        .position(labelPoint)
                        .opacity(dimmed ? 0.55 : 1)
                }

                Circle()
                    .stroke(Color.white, lineWidth: size * 0.024)

                Circle()
                    .fill(Color(hex: "#BDBDBD"))
                    .frame(width: size * 0.19, height: size * 0.19)

                Circle()
                    .fill(Color(hex: "#8E8E8E"))
                    .frame(width: size * 0.11, height: size * 0.11)
            }
        }
    }

    private func action(for direction: WheelDirection, mode: WheelMode) -> RightWheelAction {
        switch (direction, mode.usesShiftedSymbols) {
        case (.n, false): return RightWheelAction(title: "Home", systemImage: "arrow.up.to.line")
        case (.n, true): return RightWheelAction(title: "End", systemImage: "arrow.down.to.line")
        case (.ne, false): return RightWheelAction(title: ",", systemImage: nil)
        case (.ne, true): return RightWheelAction(title: "<", systemImage: nil)
        case (.e, _): return RightWheelAction(title: "Space", systemImage: nil)
        case (.se, false): return RightWheelAction(title: ".", systemImage: nil)
        case (.se, true): return RightWheelAction(title: ">", systemImage: nil)
        case (.s, false): return RightWheelAction(title: "Enter", systemImage: "return")
        case (.s, true): return RightWheelAction(title: "New Line", systemImage: "return")
        case (.sw, _): return RightWheelAction(title: "Shift", systemImage: "shift.fill")
        case (.w, _): return RightWheelAction(title: "Backspace", systemImage: "delete.left")
        case (.nw, false): return RightWheelAction(title: "Caps", systemImage: "capslock.fill")
        case (.nw, true): return RightWheelAction(title: "Caps Off", systemImage: "capslock.fill")
        case (.none, _): return RightWheelAction(title: "", systemImage: nil)
        }
    }
}

private struct RightActionLabel: View {
    let action: RightWheelAction
    let size: CGFloat
    var textColor: Color = .white

    var body: some View {
        VStack(spacing: 3) {
            if let systemImage = action.systemImage {
                Image(systemName: systemImage)
                    .font(.system(size: size * 0.44, weight: .bold))
            }

            Text(action.title)
                .font(.system(size: action.title.count > 3 ? size * 0.24 : size * 0.34, weight: .bold, design: .rounded))
                .multilineTextAlignment(.center)
                .minimumScaleFactor(0.45)
                .lineLimit(2)
        }
        .foregroundStyle(textColor)
        .shadow(color: .black.opacity(0.22), radius: 1, y: 1)
        .frame(width: size * 1.4)
    }
}

private struct SectorSlice: Shape {
    let startAngle: Double
    let endAngle: Double
    let innerRadiusRatio: CGFloat
    let outerRadiusRatio: CGFloat

    func path(in rect: CGRect) -> Path {
        let radius = min(rect.width, rect.height) / 2
        let center = CGPoint(x: rect.midX, y: rect.midY)
        let innerRadius = radius * innerRadiusRatio
        let outerRadius = radius * outerRadiusRatio
        let start = Angle(degrees: startAngle)
        let end = Angle(degrees: endAngle)

        var path = Path()
        path.addArc(center: center, radius: outerRadius, startAngle: start, endAngle: end, clockwise: false)
        path.addArc(center: center, radius: innerRadius, startAngle: end, endAngle: start, clockwise: true)
        path.closeSubpath()
        return path
    }
}

private func point(in size: CGFloat, radiusRatio: CGFloat, angleDegrees: Double) -> CGPoint {
    let radius = (size / 2) * radiusRatio
    let radians = angleDegrees * Double.pi / 180.0
    return CGPoint(
        x: (size / 2) + (CGFloat(cos(radians)) * radius),
        y: (size / 2) + (CGFloat(sin(radians)) * radius)
    )
}

extension Color {
    init(hex: String) {
        let hex = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&int)
        let a, r, g, b: UInt64
        switch hex.count {
        case 3:
            (a, r, g, b) = (255, (int >> 8) * 17, (int >> 4 & 0xF) * 17, (int & 0xF) * 17)
        case 6:
            (a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
        case 8:
            (a, r, g, b) = (int >> 24, int >> 16 & 0xFF, int >> 8 & 0xFF, int & 0xFF)
        default:
            (a, r, g, b) = (1, 1, 1, 0)
        }
        self.init(
            .sRGB,
            red: Double(r) / 255,
            green: Double(g) / 255,
            blue: Double(b) / 255,
            opacity: Double(a) / 255
        )
    }
}
