package com.vatoo.erick

import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class EmojiCellUiModel(
    val displayText: String,
    val committedText: String,
    val baseGlyph: String,
    val toneVariants: List<String>,
    val contentDescription: String,
    val isEmoticon: Boolean,
) {
    val supportsTone: Boolean
        get() = toneVariants.isNotEmpty()
}

class EmoticonsAdapter(
    private val onItemClick: (EmojiCellUiModel) -> Unit,
    private val onItemLongPress: (TextView, EmojiCellUiModel) -> Unit,
) : RecyclerView.Adapter<EmoticonsAdapter.EmojiCellViewHolder>() {
    private val items = mutableListOf<EmojiCellUiModel>()

    var isDarkMode: Boolean = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun submitItems(newItems: List<EmojiCellUiModel>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiCellViewHolder {
        val textView = TextView(parent.context).apply {
            layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            gravity = Gravity.CENTER
            minHeight = parent.context.dpToPx(42)
            setPadding(parent.context.dpToPx(4), parent.context.dpToPx(8), parent.context.dpToPx(4), parent.context.dpToPx(8))
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            includeFontPadding = false
            setBackgroundResource(android.R.drawable.list_selector_background)
            isClickable = true
            isFocusable = true
        }
        return EmojiCellViewHolder(textView)
    }

    override fun onBindViewHolder(holder: EmojiCellViewHolder, position: Int) {
        holder.bind(items[position], isDarkMode, onItemClick, onItemLongPress)
    }

    override fun getItemCount(): Int = items.size

    class EmojiCellViewHolder(private val textView: TextView) : RecyclerView.ViewHolder(textView) {
        fun bind(
            item: EmojiCellUiModel,
            isDarkMode: Boolean,
            onItemClick: (EmojiCellUiModel) -> Unit,
            onItemLongPress: (TextView, EmojiCellUiModel) -> Unit,
        ) {
            textView.text = item.displayText
            textView.contentDescription = item.contentDescription
            textView.textSize = if (item.isEmoticon) 16f else 24f
            textView.typeface = Typeface.DEFAULT
            textView.setTextColor(if (isDarkMode) Color.WHITE else Color.BLACK)
            textView.setOnClickListener { onItemClick(item) }
            textView.setOnLongClickListener {
                if (!item.supportsTone) {
                    false
                } else {
                    onItemLongPress(textView, item)
                    true
                }
            }
        }
    }
}

private fun android.content.Context.dpToPx(dp: Int): Int =
    (dp * resources.displayMetrics.density).toInt()