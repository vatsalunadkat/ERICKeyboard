//
//  IOSCustomLayoutStorage.swift
//  ErickKeyBoard
//
//  Custom layout storage backed by App Group UserDefaults.
//

import Foundation
import SharedKeyboard

/// Implements `CustomLayoutStorage` from the KMP shared module using App Group UserDefaults.
class IOSCustomLayoutStorage: CustomLayoutStorage {
    private static let appGroupDefaults = UserDefaults(suiteName: "group.com.vatoo.erick") ?? .standard
    private static let key = "custom_layouts_json"

    func loadAllLayoutsJson() -> String {
        return Self.appGroupDefaults.string(forKey: Self.key) ?? ""
    }

    func saveAllLayoutsJson(json: String) {
        Self.appGroupDefaults.set(json, forKey: Self.key)
    }
}
