import Foundation
import SharedKeyboard

struct EmojiCategory: Identifiable, Decodable, Hashable {
    let id: String
    let displayKey: String
    let items: [EmojiCatalogItem]
}

struct EmojiCatalogItem: Identifiable, Decodable, Hashable {
    let codepoints: String?
    let text: String?
    let baseGlyph: String
    let hasSkinTone: Bool?
    let toneVariants: [String]
    let name: String

    var id: String { baseGlyph }
    var supportsTone: Bool { !toneVariants.isEmpty }
}

struct EmojiCatalogLookupEntry {
    let categoryId: String
    let item: EmojiCatalogItem
}

enum EmojiCatalog {
    static let recentTabId = "recent"
    static let orderedCategoryIds = [
        "smileys",
        "people",
        "animals",
        "food",
        "travel",
        "activities",
        "objects",
        "symbols",
        "flags",
        "emoticons",
    ]

    static let categories: [EmojiCategory] = loadCategories()

    static let categoryById: [String: EmojiCategory] = Dictionary(
        uniqueKeysWithValues: categories.map { ($0.id, $0) }
    )

    static let itemLookupByGlyph: [String: EmojiCatalogLookupEntry] = {
        var lookup: [String: EmojiCatalogLookupEntry] = [:]
        for category in categories {
            for item in category.items {
                let entry = EmojiCatalogLookupEntry(categoryId: category.id, item: item)
                lookup[item.baseGlyph] = entry
                for toneVariant in item.toneVariants {
                    lookup[toneVariant] = entry
                }
            }
        }
        return lookup
    }()

    private static func loadCategories() -> [EmojiCategory] {
        let payload = EmojiCatalogPayload.shared.load()
        guard let data = payload.data(using: .utf8) else {
            return []
        }
        return (try? JSONDecoder().decode([EmojiCategory].self, from: data)) ?? []
    }
}