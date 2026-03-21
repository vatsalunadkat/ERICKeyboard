//
// ControllerBridge.swift
// The host app reads physical game controllers (DualShock 4, etc.) and shares state via App Group for the keyboard extension.
// The keyboard extension runs in a separate process and may not directly access GCController, so this bridge is needed.
//

import Foundation
import UIKit
import GameController

/// Runs in the host app; reads controller stick data and writes to App Group for the keyboard extension to read.
final class ControllerBridge {
    static let shared = ControllerBridge()
    
    private static let appGroupId = "group.com.vatoo.erick"
    
    private var displayLink: CADisplayLink?
    private var currentController: GCController?
    private var hasRegisteredObservers = false
    
    private let deadZone: Float = 0.25
    private let scale: Float = 80
    
    private init() {}
    
    private var appGroupDefaults: UserDefaults? {
        UserDefaults(suiteName: Self.appGroupId)
    }
    
    /// Called when the app enters the foreground
    func start() {
        if !hasRegisteredObservers {
            NotificationCenter.default.addObserver(
                self,
                selector: #selector(controllerDidConnect),
                name: .GCControllerDidConnect,
                object: nil
            )
            NotificationCenter.default.addObserver(
                self,
                selector: #selector(controllerDidDisconnect),
                name: .GCControllerDidDisconnect,
                object: nil
            )
            hasRegisteredObservers = true
        }
        GCController.startWirelessControllerDiscovery {}
        setupCurrentController()
    }
    
    /// Called when the app enters the background (optional, reduces battery usage)
    func stop() {
        displayLink?.invalidate()
        displayLink = nil
        currentController = nil
        clearSharedState()
    }
    
    @objc private func controllerDidConnect(_ note: Notification) {
        setupCurrentController()
    }
    
    @objc private func controllerDidDisconnect(_ note: Notification) {
        currentController = nil
        displayLink?.invalidate()
        displayLink = nil
        clearSharedState()
    }
    
    private func setupCurrentController() {
        guard let controller = GCController.controllers().first,
              controller.extendedGamepad != nil else { return }
        
        if currentController === controller, displayLink != nil { return }
        currentController = controller
        
        displayLink?.invalidate()
        let link = CADisplayLink(target: self, selector: #selector(tick))
        link.add(to: .main, forMode: .common)
        displayLink = link
    }
    
    @objc private func tick() {
        guard let controller = currentController,
              let extended = controller.extendedGamepad,
              let defaults = appGroupDefaults else { return }
        
        let leftX = extended.leftThumbstick.xAxis.value
        let leftY = extended.leftThumbstick.yAxis.value
        let rightX = extended.rightThumbstick.xAxis.value
        let rightY = extended.rightThumbstick.yAxis.value
        
        let (lnx, lny) = applyDeadZone(x: leftX, y: leftY)
        let (rnx, rny) = applyDeadZone(x: rightX, y: rightY)
        
        defaults.set(lnx, forKey: "controller_left_x")
        defaults.set(lny, forKey: "controller_left_y")
        defaults.set(rnx, forKey: "controller_right_x")
        defaults.set(rny, forKey: "controller_right_y")
        defaults.set(Date().timeIntervalSince1970, forKey: "controller_timestamp")
        defaults.synchronize()
    }
    
    private func applyDeadZone(x: Float, y: Float) -> (Float, Float) {
        var nx = x
        var ny = y
        let mag = sqrt(nx * nx + ny * ny)
        if mag > deadZone {
            let scaleFactor = (mag - deadZone) / (1 - deadZone)
            nx = (nx / mag) * scaleFactor
            ny = (ny / mag) * scaleFactor
        } else {
            nx = 0
            ny = 0
        }
        return (nx, ny)
    }
    
    private func clearSharedState() {
        guard let defaults = appGroupDefaults else { return }
        defaults.set(Float(0), forKey: "controller_left_x")
        defaults.set(Float(0), forKey: "controller_left_y")
        defaults.set(Float(0), forKey: "controller_right_x")
        defaults.set(Float(0), forKey: "controller_right_y")
        defaults.set(Date().timeIntervalSince1970, forKey: "controller_timestamp")
        defaults.synchronize()
    }
}
