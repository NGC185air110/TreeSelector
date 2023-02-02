package com.dlc.dlctreeselector.adapter

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.dlc.dlctreeselector.R
import com.dlc.dlctreeselector.model.DlcTree


/**
 * title：
 * description：
 * author：dinglicheng on 2022/11/18 10:03
 */
class TreeAdapter<T : DlcTree> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val ONE_ITEM = 1
    private val TWO_ITEM = 2

    var data: ArrayList<T>? = null
    private var context: Context? = null

    @DrawableRes
    var pitchOn = R.drawable.bg_item_text_un

    @DrawableRes
    var pitchOff = R.drawable.bg_item_text

    var itemMarginEnd = 23F
    var itemMarginBottom = 10F


    var onChickTitle: (() -> Unit)? = null
    var onChickItem: ((data: T?) -> Unit)? = null

    fun setDate(
        context: Context,
        onChickTitle: (() -> Unit)? = null,
        onChickItem: (data: T?) -> Unit,
        pitchOn: Int? = null,
        pitchOff: Int? = null,
        itemMarginEnd: Float = 23F,
        itemMarginBottom: Float = 10F,
    ) {
        this.context = context
        this.onChickTitle = onChickTitle
        this.onChickItem = onChickItem
        if (pitchOn != null) {
            this.pitchOn = pitchOn
        }
        if (pitchOff != null) {
            this.pitchOff = pitchOff
        }
        this.itemMarginEnd = itemMarginEnd
        this.itemMarginBottom = itemMarginBottom
    }

    class TreeAdapterTitleViewHolder : RecyclerView.ViewHolder {
        var tvLeaf: TextView? = null

        constructor(itemView: View) : super(itemView) {
            tvLeaf = itemView.findViewById(R.id.tv_leaf)
        }
    }

    class TreeAdapterItemViewHolder : RecyclerView.ViewHolder {
        var tvText: TextView? = null

        constructor(itemView: View) : super(itemView) {
            tvText = itemView.findViewById(R.id.tv_text)
        }
    }

    override fun getItemCount(): Int = data?.size ?: 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var mInflater: LayoutInflater = LayoutInflater.from(context)
        var holder: RecyclerView.ViewHolder? = null
        holder = if (ONE_ITEM == viewType) {
            val v = mInflater.inflate(R.layout.item_leaf, parent, false)
            TreeAdapterTitleViewHolder(v)
        } else {
            val v = mInflater.inflate(R.layout.item_text, parent, false)
            TreeAdapterItemViewHolder(v)
        }
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TreeAdapterTitleViewHolder) {
            holder.tvLeaf?.text = data?.get(position).toString()
            holder.tvLeaf?.setOnClickListener {
                if (data?.get(position)?.live == 1) {
                    onChickTitle?.invoke()
                } else {
                    onChickItem?.invoke(data?.get(position))
                }
            }
        } else if (holder is TreeAdapterItemViewHolder) {
            holder.tvText?.text = data?.get(position).toString()
            holder.tvText?.setOnClickListener {
                onChickItem?.invoke(data?.get(position))
                if (data?.get(position)?.isChick == true) {
                    holder.tvText?.setBackgroundResource(pitchOff)
                    data?.get(position)?.isChick = false
                } else {
                    holder.tvText?.setBackgroundResource(pitchOn)
                    data?.get(position)?.isChick = true
                }
            }
            if (data?.get(position)?.isChick == true) {
                holder.tvText?.setBackgroundResource(pitchOn)
            } else {
                holder.tvText?.setBackgroundResource(pitchOff)
            }
            val lp = holder.tvText?.layoutParams as RecyclerView.LayoutParams
            lp.setMargins(0, 0, dp2px(itemMarginEnd), dp2px(itemMarginBottom))
            holder.tvText?.layoutParams = lp
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (data?.get(position)?.live == 1) {
            ONE_ITEM
        } else {
            TWO_ITEM
        }
    }

    private fun dp2px(dpValue: Float): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

}