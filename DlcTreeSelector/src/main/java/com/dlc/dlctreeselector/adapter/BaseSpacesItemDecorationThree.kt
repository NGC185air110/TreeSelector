package com.dlc.dlctreeselector.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

/**
 * title：通用表格分割
 * description：
 * author：dinglicheng on 2023/10/7 17:28
 */
class BaseSpacesItemDecorationThree(
    private val gridSize: Int,
    private val spaceH: Int = 0,//中间间距
    private val spaceV: Int = 0,//第二行开始上下间距
    private val edgeH: Int = 0, // 网格两边的间距
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State,
    ) {

        val position = parent.getChildAdapterPosition(view)

        // 获取第几列
        val column = position % gridSize
        // 第几行
        val row: Int = position / gridSize
        if (row != 0) { // 设置top
            outRect.top = spaceV
        }

        // p为每个Item都需要减去的间距
        val p = (2 * edgeH + (gridSize - 1) * spaceH) * 1f / gridSize
        val left = edgeH + column * (spaceH - p)
        val right = p - left

        outRect.left = left.roundToInt()
        outRect.right = right.roundToInt()

    }
}