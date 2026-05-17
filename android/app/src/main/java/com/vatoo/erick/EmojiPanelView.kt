package com.vatoo.erick

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vatoo.erick.shared.EmojiCatalogPayload
import org.json.JSONArray
import kotlin.math.max

class EmojiPanelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    interface Listener {
        fun onCommitText(text: String)
        fun onReturnToKeyboard()
        fun onBackspacePressStarted()
        fun onBackspacePressEnded()
    }

    private val tabScrollView = HorizontalScrollView(context).apply {
        isHorizontalScrollBarEnabled = false
        overScrollMode = View.OVER_SCROLL_NEVER
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    private val tabStrip = LinearLayout(context).apply {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        setPadding(dp(4), dp(4), dp(4), dp(4))
    }

    private val gridLayoutManager = GridLayoutManager(context, 8)

    private val recyclerView = RecyclerView(context).apply {
        layoutManager = gridLayoutManager
        overScrollMode = View.OVER_SCROLL_IF_CONTENT_SCROLLS
        itemAnimator = null
        clipChildren = true
        clipToPadding = true
        setPadding(dp(2), dp(2), dp(2), dp(4))
    }

    private val gridAdapter = EmoticonsAdapter(
        onItemClick = { item -> commitItem(item) },
        onItemLongPress = { anchor, item -> showTonePopup(anchor, item) },
    )

    private val bottomBar = LinearLayout(context).apply {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        setPadding(dp(8), dp(4), dp(8), dp(4))
    }

    private val abcButton = TextView(context).apply {
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        gravity = Gravity.CENTER
        minWidth = dp(48)
        minHeight = dp(32)
        setPadding(dp(12), dp(6), dp(12), dp(6))
        text = "ABC"
        textSize = 13f
        isClickable = true
        isFocusable = true
        setOnClickListener { listener?.onReturnToKeyboard() }
    }

    private val bottomSpacer = View(context).apply {
        layoutParams = LayoutParams(0, 0, 1f)
    }

    private val backspaceButton = TextView(context).apply {
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        gravity = Gravity.CENTER
        minWidth = dp(48)
        minHeight = dp(32)
        setPadding(dp(12), dp(6), dp(12), dp(6))
        text = "⌫"
        textSize = 20f
        isClickable = true
        isFocusable = true
        setOnTouchListener { view, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    listener?.onBackspacePressStarted()
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    listener?.onBackspacePressEnded()
                    view.performClick()
                    true
                }
                else -> false
            }
        }
    }

    private var listener: Listener? = null
    private var currentLanguageKey: String = PreferencesManager.LANGUAGE_ENGLISH
    private var isDarkMode: Boolean = false
    private var currentTypeface: Typeface? = null
    private var currentTabId: String = defaultTabId(emptyList())
    private var recentEmojis: List<String> = emptyList()
    private val preferredToneByBase = mutableMapOf<String, String>()
    private val tabViews = linkedMapOf<String, TextView>()
    private var tonePopupWindow: PopupWindow? = null

    init {
        orientation = VERTICAL
        clipChildren = true
        clipToPadding = true

        tabScrollView.addView(tabStrip)
        addView(tabScrollView)

        recyclerView.adapter = gridAdapter
        addView(recyclerView, LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f))

        bottomBar.addView(abcButton)
        bottomBar.addView(bottomSpacer)
        bottomBar.addView(backspaceButton)
        addView(bottomBar, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))

        recyclerView.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateSpanCount()
        }

        rebuildTabs()
        updateSpanCount()
        applyVisualStyle()
        refreshGrid()
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    fun setLanguageKey(languageKey: String) {
        if (currentLanguageKey == languageKey) {
            return
        }
        currentLanguageKey = languageKey
        rebuildTabs()
        applyVisualStyle()
        refreshGrid()
    }

    fun setDarkMode(isDarkMode: Boolean) {
        if (this.isDarkMode == isDarkMode) {
            return
        }
        this.isDarkMode = isDarkMode
        applyVisualStyle()
    }

    fun setTextTypeface(typeface: Typeface?) {
        currentTypeface = typeface
        applyVisualStyle()
    }

    fun setRecentEmojis(items: List<String>) {
        recentEmojis = items
        if (currentTabId == TAB_RECENT || (currentTabId == TAB_RECENT && items.isEmpty())) {
            refreshGrid()
        }
    }

    fun dismissTonePicker() {
        tonePopupWindow?.dismiss()
        tonePopupWindow = null
    }

    override fun onDetachedFromWindow() {
        dismissTonePicker()
        super.onDetachedFromWindow()
    }

    private fun rebuildTabs() {
        tabStrip.removeAllViews()
        tabViews.clear()

        orderedTabs().forEach { tabId ->
            val labelKey = if (tabId == TAB_RECENT) {
                "emoji_tab_recent"
            } else {
                categoryById[tabId]?.displayKey ?: tabId
            }

            val tabView = TextView(context).apply {
                layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                    marginEnd = dp(4)
                }
                gravity = Gravity.CENTER
                minHeight = dp(32)
                setPadding(dp(12), dp(7), dp(12), dp(7))
                text = erickText(currentLanguageKey, labelKey)
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    currentTabId = tabId
                    refreshTabSelection()
                    refreshGrid()
                }
            }

            tabViews[tabId] = tabView
            tabStrip.addView(tabView)
        }

        refreshTabSelection()
    }

    private fun refreshTabSelection() {
        tabViews.forEach { (tabId, tabView) ->
            val isSelected = tabId == currentTabId
            tabView.background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = dp(16).toFloat()
                setColor(
                    when {
                        isSelected && isDarkMode -> Color.parseColor("#3C4043")
                        isSelected -> Color.WHITE
                        isDarkMode -> Color.parseColor("#242628")
                        else -> Color.parseColor("#DDE4E8")
                    },
                )
            }
            tabView.setTextColor(
                when {
                    isSelected && isDarkMode -> Color.WHITE
                    isSelected -> Color.parseColor("#1E1E1E")
                    isDarkMode -> Color.parseColor("#D6D9DC")
                    else -> Color.parseColor("#3A3D40")
                },
            )
            tabView.typeface = currentTypeface ?: Typeface.DEFAULT_BOLD
        }
    }

    private fun refreshGrid() {
        gridAdapter.submitItems(buildCellsForCurrentTab())
    }

    private fun buildCellsForCurrentTab(): List<EmojiCellUiModel> {
        return if (currentTabId == TAB_RECENT) {
            recentEmojis.map { recent ->
                buildRecentCell(recent)
            }
        } else {
            categoryById[currentTabId]?.items.orEmpty().map { item ->
                val preferredGlyph = preferredToneByBase[item.baseGlyph] ?: item.baseGlyph
                EmojiCellUiModel(
                    displayText = preferredGlyph,
                    committedText = preferredGlyph,
                    baseGlyph = item.baseGlyph,
                    toneVariants = item.toneVariants,
                    contentDescription = item.name.ifBlank { preferredGlyph },
                    isEmoticon = item.categoryId == "emoticons",
                )
            }
        }
    }

    private fun buildRecentCell(recent: String): EmojiCellUiModel {
        val lookupItem = itemLookupByGlyph[recent]
        return if (lookupItem == null) {
            EmojiCellUiModel(
                displayText = recent,
                committedText = recent,
                baseGlyph = recent,
                toneVariants = emptyList(),
                contentDescription = recent,
                isEmoticon = true,
            )
        } else {
            EmojiCellUiModel(
                displayText = recent,
                committedText = recent,
                baseGlyph = lookupItem.baseGlyph,
                toneVariants = lookupItem.toneVariants,
                contentDescription = lookupItem.name.ifBlank { recent },
                isEmoticon = lookupItem.categoryId == "emoticons",
            )
        }
    }

    private fun commitItem(item: EmojiCellUiModel) {
        listener?.onCommitText(item.committedText)
    }

    private fun showTonePopup(anchor: TextView, item: EmojiCellUiModel) {
        if (!item.supportsTone) {
            return
        }

        dismissTonePicker()

        val popupContent = LinearLayout(context).apply {
            orientation = HORIZONTAL
            setPadding(dp(6), dp(6), dp(6), dp(6))
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = dp(20).toFloat()
                setColor(if (isDarkMode) Color.parseColor("#2C2F33") else Color.WHITE)
            }
            elevation = dp(8).toFloat()
        }

        val options = buildList {
            add(item.baseGlyph)
            addAll(item.toneVariants)
        }

        options.forEach { option ->
            val optionView = TextView(context).apply {
                layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                    marginStart = dp(2)
                    marginEnd = dp(2)
                }
                gravity = Gravity.CENTER
                minWidth = dp(36)
                minHeight = dp(36)
                text = option
                textSize = 24f
                setPadding(dp(8), dp(4), dp(8), dp(4))
                setOnClickListener {
                    if (option == item.baseGlyph) {
                        preferredToneByBase.remove(item.baseGlyph)
                    } else {
                        preferredToneByBase[item.baseGlyph] = option
                    }
                    tonePopupWindow?.dismiss()
                    listener?.onCommitText(option)
                    refreshGrid()
                }
            }
            popupContent.addView(optionView)
        }

        val popupWindow = PopupWindow(
            popupContent,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            false,
        ).apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            isOutsideTouchable = true
            isFocusable = false
            inputMethodMode = PopupWindow.INPUT_METHOD_NOT_NEEDED
            softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
            isClippingEnabled = false
            elevation = dp(8).toFloat()
            animationStyle = 0
            setOnDismissListener {
                if (tonePopupWindow === this) {
                    tonePopupWindow = null
                }
            }
        }

        popupContent.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        val popupWidth = popupContent.measuredWidth
        val popupHeight = popupContent.measuredHeight
        val anchorLocation = IntArray(2)
        anchor.getLocationInWindow(anchorLocation)
        val rootView = anchor.rootView
        val windowFrame = Rect().apply {
            rootView.getWindowVisibleDisplayFrame(this)
        }
        val horizontalMargin = dp(8)
        val verticalMargin = dp(4)
        val preferredX = anchorLocation[0] + (anchor.width / 2) - (popupWidth / 2)
        val minX = windowFrame.left + horizontalMargin
        val maxX = windowFrame.right - popupWidth - horizontalMargin
        val x = if (maxX >= minX) preferredX.coerceIn(minX, maxX) else preferredX
        val preferredAboveY = anchorLocation[1] - popupHeight - dp(8)
        val preferredBelowY = anchorLocation[1] + anchor.height + dp(8)
        val minY = windowFrame.top + verticalMargin
        val maxY = windowFrame.bottom - popupHeight - verticalMargin
        val y = when {
            preferredAboveY >= minY -> preferredAboveY
            preferredBelowY <= maxY -> preferredBelowY
            maxY >= minY -> preferredAboveY.coerceIn(minY, maxY)
            else -> preferredAboveY
        }

        tonePopupWindow = popupWindow
        popupWindow.showAtLocation(rootView, Gravity.NO_GRAVITY, x, y)
    }

    private fun applyVisualStyle() {
        val panelSurfaceColor = if (isDarkMode) Color.parseColor("#1C1E20") else Color.parseColor("#EEF2F4")
        setBackgroundColor(panelSurfaceColor)
        gridAdapter.isDarkMode = isDarkMode

        tabScrollView.setBackgroundColor(panelSurfaceColor)
        tabStrip.setBackgroundColor(panelSurfaceColor)
        recyclerView.setBackgroundColor(panelSurfaceColor)
        bottomBar.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dp(18).toFloat()
            setColor(if (isDarkMode) Color.parseColor("#26282B") else Color.parseColor("#F5F7F8"))
        }

        val buttonTextColor = if (isDarkMode) Color.WHITE else Color.parseColor("#1E1E1E")
        val buttonBackground = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dp(16).toFloat()
            setColor(if (isDarkMode) Color.parseColor("#3A3D40") else Color.WHITE)
        }

        listOf(abcButton, backspaceButton).forEach { button ->
            button.setTextColor(buttonTextColor)
            button.background = buttonBackground.constantState?.newDrawable()?.mutate()
            button.typeface = currentTypeface ?: Typeface.DEFAULT_BOLD
        }

        abcButton.contentDescription = erickText(currentLanguageKey, "emoji_button_back_abc")
        backspaceButton.contentDescription = erickText(currentLanguageKey, "Backspace")
        refreshTabSelection()
    }

    private fun orderedTabs(): List<String> = buildList {
        add(TAB_RECENT)
        TAB_ORDER.forEach { tabId ->
            if (categoryById.containsKey(tabId)) {
                add(tabId)
            }
        }
    }

    private fun defaultTabId(recents: List<String>): String = if (recents.isNotEmpty()) TAB_RECENT else "smileys"

    private fun updateSpanCount() {
        if (width == 0) {
            return
        }
        val targetSpan = max(6, width / dp(42))
        if (gridLayoutManager.spanCount != targetSpan) {
            gridLayoutManager.spanCount = targetSpan
        }
    }

    private fun dp(value: Int): Int =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), resources.displayMetrics).toInt()

    companion object {
        private const val TAB_RECENT = "recent"
        private val TAB_ORDER = listOf(
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
        )

        private val catalog: List<EmojiCatalogCategory> by lazy {
            parseEmojiCatalog(EmojiCatalogPayload.load())
        }

        private val categoryById: Map<String, EmojiCatalogCategory> by lazy {
            catalog.associateBy { it.id }
        }

        private val itemLookupByGlyph: Map<String, EmojiCatalogItem> by lazy {
            buildMap {
                catalog.forEach { category ->
                    category.items.forEach { item ->
                        put(item.baseGlyph, item)
                        item.toneVariants.forEach { variant ->
                            put(variant, item)
                        }
                    }
                }
            }
        }

        private fun parseEmojiCatalog(serialized: String): List<EmojiCatalogCategory> {
            return try {
                val categories = JSONArray(serialized)
                buildList {
                    for (categoryIndex in 0 until categories.length()) {
                        val categoryObject = categories.optJSONObject(categoryIndex) ?: continue
                        val categoryId = categoryObject.optString("id")
                        val displayKey = categoryObject.optString("displayKey")
                        val itemsArray = categoryObject.optJSONArray("items") ?: JSONArray()
                        val items = buildList {
                            for (itemIndex in 0 until itemsArray.length()) {
                                val itemObject = itemsArray.optJSONObject(itemIndex) ?: continue
                                val toneVariantsArray = itemObject.optJSONArray("toneVariants") ?: JSONArray()
                                val toneVariants = buildList {
                                    for (toneIndex in 0 until toneVariantsArray.length()) {
                                        val tone = toneVariantsArray.optString(toneIndex)
                                        if (tone.isNotBlank()) {
                                            add(tone)
                                        }
                                    }
                                }
                                add(
                                    EmojiCatalogItem(
                                        categoryId = categoryId,
                                        baseGlyph = itemObject.optString("baseGlyph"),
                                        toneVariants = toneVariants,
                                        name = itemObject.optString("name"),
                                    ),
                                )
                            }
                        }
                        add(
                            EmojiCatalogCategory(
                                id = categoryId,
                                displayKey = displayKey,
                                items = items,
                            ),
                        )
                    }
                }
            } catch (_: Exception) {
                emptyList()
            }
        }
    }
}

private data class EmojiCatalogCategory(
    val id: String,
    val displayKey: String,
    val items: List<EmojiCatalogItem>,
)

private data class EmojiCatalogItem(
    val categoryId: String,
    val baseGlyph: String,
    val toneVariants: List<String>,
    val name: String,
)