//
//  IOSCustomLayoutStorage.swift
//  ERICK
//
//  Custom layout storage backed by App Group UserDefaults.
//  Shared between main app and keyboard extension via the app group.
//

import Foundation
import SharedKeyboard

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
