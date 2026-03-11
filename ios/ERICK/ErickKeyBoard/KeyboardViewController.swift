import UIKit
import SwiftUI
import Combine
import SharedKeyboard // 🚨 引入我们刚刚打包好的 KMP 大脑！

// 1. 状态桥梁：由于 SwiftUI 是响应式的，我们需要一个 ViewModel 来实时更新预览文字
class KeyboardViewModel: ObservableObject {
    @Published var previewText: String = ""
}

// 2. SwiftUI 键盘容器：把左右两个摇杆横向排列
struct KeyboardContainerView: View {
    @ObservedObject var viewModel: KeyboardViewModel
    // 闭包回调参数：dx, dy, isLeft, isDownOrMove, isUp
    var onTouch: (Float, Float, Bool, Bool, Bool) -> Void
    
    @State private var showSettings = false // 控制设置页面的显示

    var body: some View {
        ZStack {
            VStack(spacing: 0) {
                // 顶部工具条：放设置按钮等
                HStack {
                    Spacer()
                    Button(action: {
                        withAnimation {
                            showSettings = true
                        }
                    }) {
                        Image(systemName: "gear")
                            .font(.title2)
                            .foregroundColor(.gray)
                            .padding(.horizontal)
                            .padding(.top, 4)
                    }
                }
                
                // 摇杆控制区
                HStack(spacing: 16) {
                    // 左摇杆
                    JoystickView(isRightSide: false, previewText: "") { dx, dy, isDownOrMove, isUp in
                        onTouch(dx, dy, true, isDownOrMove, isUp)
                    }
                    
                    // 右摇杆 (绑定 ViewModel 里的预览文字)
                    JoystickView(isRightSide: true, previewText: viewModel.previewText) { dx, dy, isDownOrMove, isUp in
                        onTouch(dx, dy, false, isDownOrMove, isUp)
                    }
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .padding(.horizontal, 16)
                .padding(.bottom, 8)
            }
            .background(Color(hex: "#ECEFF1")) // 浅蓝色背景
            
            // 覆盖设置页面
            if showSettings {
                SettingsView(onClose: {
                    withAnimation {
                        showSettings = false
                    }
                })
                .transition(.move(edge: .bottom))
                .zIndex(1)
            }
        }
    }
}

// 3. iOS 输入法核心控制器
class KeyboardViewController: UIInputViewController, KeyboardActionDelegate {

    var stateMachine: KeyboardStateMachine!
    var viewModel = KeyboardViewModel()

    override func viewDidLoad() {
        super.viewDidLoad()
        
        // --- 唤醒跨平台大脑 ---
        // Kotlin 的全局函数在 Swift 里会自动被放到带 'Kt' 后缀的命名空间下
//        stateMachine = // Swift 现在会极其自然地调用我们刚刚写的那个次级构造函数！
        // 使用我们在 Kotlin 里建好的工厂 (KeyboardFactory) 去拿货
        stateMachine = KeyboardFactory.shared.createEngine(delegate: self)
        
        // --- UI 挂载与闭包打通 ---
        let containerView = KeyboardContainerView(viewModel: viewModel) { [weak self] dx, dy, isLeft, isDown, isUp in
            self?.handleTouch(dx: dx, dy: dy, isLeft: isLeft, isDown: isDown, isUp: isUp)
        }
        
        // 使用 UIHostingController 把 SwiftUI 包装成传统的 UIKit View
        let hostingController = UIHostingController(rootView: containerView)
        hostingController.view.translatesAutoresizingMaskIntoConstraints = false
        hostingController.view.backgroundColor = .clear
        
        self.addChild(hostingController)
        self.view.addSubview(hostingController.view)
        hostingController.didMove(toParent: self)
        
        // 设置 iOS 的自动布局约束（撑满全屏，高度定为盲打舒适的 280）
        let heightConstraint = self.view.heightAnchor.constraint(equalToConstant: 280)
        heightConstraint.priority = .init(999)
        
        // 2. 激活所有约束
        NSLayoutConstraint.activate([
            hostingController.view.leftAnchor.constraint(equalTo: self.view.leftAnchor),
            hostingController.view.rightAnchor.constraint(equalTo: self.view.rightAnchor),
            hostingController.view.topAnchor.constraint(equalTo: self.view.topAnchor),
            hostingController.view.bottomAnchor.constraint(equalTo: self.view.bottomAnchor),
            heightConstraint
        ])
    }
    
    // --- 核心分发：将 iOS 触摸数据喂给 Kotlin 大脑 ---
    func handleTouch(dx: Float, dy: Float, isLeft: Bool, isDown: Bool, isUp: Bool) {
        stateMachine.handleTouch(x: dx, y: dy, isLeft: isLeft, actionDownOrMove: isDown, actionUp: isUp)
        
        // 从大脑获取最新预览并更新 UI
        viewModel.previewText = stateMachine.getPreviewText()
    }

    // ==========================================
    // 实现 Kotlin 大脑的代理方法 (Action Delegate)
    // ==========================================

    func commitText(text: String) {
        self.textDocumentProxy.insertText(text)
    }

    func sendInputAction(action: InputAction) {
        // Kotlin 的枚举在 Swift 中会自动变成小写驼峰式
        switch action {
        case .space:
            self.textDocumentProxy.insertText(" ")
        case .enter:
            self.textDocumentProxy.insertText("\n")
        case .backspace, .deleteForward:
            // iOS 键盘 API 没有向后删除，统一使用系统的 deleteBackward
            self.textDocumentProxy.deleteBackward()
        case .tab:
            self.textDocumentProxy.insertText("\t")
            
        // --- 移动光标逻辑 ---
        case .dpadLeft:
            self.textDocumentProxy.adjustTextPosition(byCharacterOffset: -1)
        case .dpadRight:
            self.textDocumentProxy.adjustTextPosition(byCharacterOffset: 1)
        default:
            // 提示：iOS 第三方键盘由于系统安全限制，无法完美模拟上下键 (不知道上一行有多少字)
            // 所以 DPAD_UP / DOWN 等在这里暂时忽略
            break
        }
    }
}

