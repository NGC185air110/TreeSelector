package com.dlc.dlctreeselector.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dlc.dlctreeselector.R

/**
 * title：
 * description：
 * author：dinglicheng on 2023/4/20 15:21
 */
class LicensePlateNumberAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var data: ArrayList<String>? = null
    private var context: Context? = null
    var onChickItem: ((data: String) -> Unit)? = null

    fun setDate(
        data: ArrayList<String>,
        context: Context,
        onChickItem: ((data: String) -> Unit),
    ) {
        this.data = data
        this.context = context
        this.onChickItem = onChickItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        LicensePlateNumberAdapterViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_keyboard, parent, false)
        )

    override fun getItemCount(): Int = data?.size ?: 0

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val mHolder = holder as LicensePlateNumberAdapterViewHolder
        mHolder.tvText.text = data?.get(position) ?: ""

        mHolder.tvText.setOnClickListener {
            onChickItem?.invoke(data?.get(position) ?: "")
        }
    }

    class LicensePlateNumberAdapterViewHolder : RecyclerView.ViewHolder {
        var tvText: TextView

        constructor(itemView: View) : super(itemView) {
            tvText = itemView.findViewById(R.id.tv_text)
        }
    }
}