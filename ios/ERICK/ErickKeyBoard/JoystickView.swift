import SwiftUI

struct JoystickView: View {
    // --- 核心状态变量 ---
    var isRightSide: Bool
    var previewText: String
    
    // --- 与外壳通信的闭包 (等价于 Android 的回调) ---
    // 参数: dx, dy, isDownOrMove, isUp
    var onTouch: ((Float, Float, Bool, Bool) -> Void)?
    
    // 记录滑块当前的偏移量
    @State private var thumbOffset: CGSize = .zero
    
    var body: some View {
        GeometryReader { geometry in
            // 计算可用空间的核心圆心和半径
            let width = geometry.size.width
            let height = geometry.size.height
            let minDimension = min(width, height)
            
            // 完美复刻 Android 端的比例逻辑
            let baseRadius = (minDimension / 2.0) * 0.85
            let thumbRadius = baseRadius / 3.0
            let maxThumbDistance = baseRadius - thumbRadius
            
            // ZStack 会自动把里面的所有东西居中叠放
            ZStack {
                // 1. 画底座
                Circle()
                    .fill(Color(hex: "#E0E0E0")) // 浅灰色
                    .frame(width: baseRadius * 2, height: baseRadius * 2)
                
                // 2. 画预览文字 (仅限右摇杆且有文字时)
                if isRightSide && !previewText.isEmpty {
                    Text(previewText)
                        .font(.system(size: 44, weight: .bold)) // 醒目的字号
                        .foregroundColor(Color(hex: "#1976D2")) // 蓝色
                        // SwiftUI 的 ZStack 默认就是绝对居中，无需像 Android 那样算文字基线
                }
                
                // 3. 画滑块 (带拖拽手势)
                Circle()
                    .fill(Color(hex: "#757575")) // 深灰色
                    .frame(width: thumbRadius * 2, height: thumbRadius * 2)
                    .offset(thumbOffset)
                    .gesture(
                        DragGesture(minimumDistance: 0) // minimumDistance: 0 保证按下瞬间就触发
                            .onChanged { value in
                                // --- 等价于 Android 的 ACTION_DOWN 和 ACTION_MOVE ---
                                let dx = value.translation.width
                                let dy = value.translation.height
                                
                                // 勾股定理计算距离
                                let distance = sqrt(dx * dx + dy * dy)
                                
                                var clampedDx = dx
                                var clampedDy = dy
                                
                                // 防越界钳制逻辑 (和 Android 端的数学原理一模一样)
                                if distance > maxThumbDistance {
                                    let ratio = maxThumbDistance / distance
                                    clampedDx = dx * ratio
                                    clampedDy = dy * ratio
                                }
                                
                                // 实时更新 UI 偏移量
                                thumbOffset = CGSize(width: clampedDx, height: clampedDy)
                                
                                // 把数据传给 ViewController (相当于呼叫 Shared 大脑)
                                onTouch?(Float(clampedDx), Float(clampedDy), true, false)
                            }
                            .onEnded { _ in
                                // --- 等价于 Android 的 ACTION_UP ---
                                // 加入一个 Q 弹的物理回弹动画
                                withAnimation(.spring(response: 0.2, dampingFraction: 0.6)) {
                                    thumbOffset = .zero
                                }
                                
                                // 通知大脑：手指抬起了
                                onTouch?(0, 0, false, true)
                            }
                    )
            }
            .frame(width: width, height: height) // 确保 ZStack 占满父容器，居中显示
        }
    }
}

// --- 辅助扩展：让 SwiftUI 支持直接使用 Hex 颜色代码 ---
extension Color {
    init(hex: String) {
        let hex = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&int)
        let a, r, g, b: UInt64
        switch hex.count {
        case 3: // RGB (12-bit)
            (a, r, g, b) = (255, (int >> 8) * 17, (int >> 4 & 0xF) * 17, (int & 0xF) * 17)
        case 6: // RGB (24-bit)
            (a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
        case 8: // ARGB (32-bit)
            (a, r, g, b) = (int >> 24, int >> 16 & 0xFF, int >> 8 & 0xFF, int & 0xFF)
        default:
            (a, r, g, b) = (1, 1, 1, 0)
        }
        self.init(
            .sRGB,
            red: Double(r) / 255,
            green: Double(g) / 255,
            blue:  Double(b) / 255,
            opacity: Double(a) / 255
        )
    }
}
