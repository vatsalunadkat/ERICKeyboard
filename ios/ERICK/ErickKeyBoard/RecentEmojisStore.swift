import Foundation

final class RecentEmojisStore {
    private static let maxItems = 32
    private let defaults: UserDefaults
    private let key = "recent_emojis"

    init(defaults: UserDefaults = UserDefaults(suiteName: "group.com.vatoo.erick") ?? .standard) {
        self.defaults = defaults
    }

    func load() -> [String] {
        guard let serialized = defaults.string(forKey: key),
              let data = serialized.data(using: .utf8),
              let decoded = try? JSONDecoder().decode([String].self, from: data)
        else {
            return []
        }

        return Array(decoded.filter { !$0.isEmpty }.prefix(Self.maxItems))
    }

    @discardableResult
    func record(_ text: String) -> [String] {
        var recents = load().filter { $0 != text }
        recents.insert(text, at: 0)
        recents = Array(recents.prefix(Self.maxItems))
        save(recents)
        return recents
    }

    func save(_ items: [String]) {
        let trimmed = Array(items.prefix(Self.maxItems))
        guard let data = try? JSONEncoder().encode(trimmed),
              let serialized = String(data: data, encoding: .utf8)
        else {
            return
        }
        defaults.set(serialized, forKey: key)
    }
}