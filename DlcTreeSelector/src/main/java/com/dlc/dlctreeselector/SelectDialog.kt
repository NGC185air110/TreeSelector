package com.dlc.dlctreeselector

import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import com.dlc.dlctreeselector.adapter.TreeAdapter
import com.dlc.dlctreeselector.databinding.DialogSelectBinding
import com.dlc.dlctreeselector.model.DlcTree
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * title：
 * description：选择用dialog
 * author：dinglicheng on 2022/11/16 10:51
 */
class SelectDialog<T : DlcTree> : BottomSheetDialogFragment() {

    //dialog最大高度
    var maxDialogHeight = -1

    //dialog背景颜色
    var dialogBackgroundDrawable = Color.TRANSPARENT

    //数据
    var data: ArrayList<T>? = null

    private val originData: ArrayList<T> by lazy { dataToItemTree(data!!) }

    //一行数量
    var spanCount = 3

    //dialogBG
    @DrawableRes
    var dialogBg = R.drawable.bg_dialog

    //是否开启树桩结构
    var isTreeArray = true

    //选中保存
    private var chickList: ArrayList<T> = ArrayList()

    //MAX选中数量
    var maximum = Int.MAX_VALUE

    var getConfirm: ((TextView) -> Unit)? = null
    var getTitle: ((TextView) -> Unit)? = null
    var getCancel: ((TextView) -> Unit)? = null

    //返回选中数值
    var BackchickList: ((ArrayList<T>) -> Unit)? = null

    //是否为必须选中
    var alwaysListNotNull = true

    //显示状态
    var dialogStyle: DialogStyle = DialogStyle.NORMAL

    var itemMarginEnd: Float = 23F
    var itemMarginBottom: Float = 10F

    inline fun builder(func: SelectDialog<T>.() -> Unit): SelectDialog<T> {
        this.func()
        return this
    }

    private lateinit var vb: DialogSelectBinding


    override fun onStart() {
        super.onStart()
        val dialog = dialog as BottomSheetDialog
        if (dialog != null) {
            val bottomSheet =
                dialog.delegate.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet!!.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT  //自定义高度
        }
        val view = view
        view!!.post {
            val parent = view.parent as View
            val params = parent.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = params.behavior
            val bottomSheetBehavior = behavior as BottomSheetBehavior<*>?
            when (maxDialogHeight) {
                0 -> {
                    bottomSheetBehavior!!.peekHeight = view.measuredHeight
                }
                -1 -> {
                    bottomSheetBehavior!!.maxHeight = this.resources.displayMetrics.heightPixels / 2
                    bottomSheetBehavior!!.peekHeight =
                        this.resources.displayMetrics.heightPixels / 2
                }
                else -> {
                    bottomSheetBehavior!!.peekHeight = dp2px(maxDialogHeight.toFloat())
                }
            }
        }

        chickList.clear()
        for (it in originData) {
            if (it.isChick) {
                chickList.add(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        vb = DialogSelectBinding.inflate(inflater, container, false)
        getConfirm?.invoke(vb.tvConfirm)
        getTitle?.invoke(vb.tvTitle)
        getCancel?.invoke(vb.tvCancel)

        vb.llBg.setBackgroundResource(dialogBg)

        when (dialogStyle) {
            DialogStyle.BOTTOM -> {
                vb.tvConfirm.visibility = View.GONE
                vb.tvConfirmBottom.visibility = View.VISIBLE
                vb.tvCancel.visibility = View.GONE
                vb.ivCancel.visibility = View.VISIBLE
                vb.line.visibility = View.GONE

                vb.rvData.setPadding(
                    vb.rvData.paddingLeft,
                    vb.rvData.paddingTop,
                    0,
                    dp2px((vb.tvConfirmBottom.height + vb.tvConfirmBottom.paddingBottom + 20).toFloat())
                )
                vb.llBg.setPadding(
                    0,
                    0,
                    0,
                    dp2px(10F)
                )
            }
            else -> {
                vb.tvConfirm.visibility = View.VISIBLE
                vb.tvConfirmBottom.visibility = View.GONE
                vb.tvCancel.visibility = View.VISIBLE
                vb.ivCancel.visibility = View.GONE
                vb.line.visibility = View.VISIBLE
                vb.rvData.setPadding(
                    vb.rvData.paddingLeft,
                    vb.rvData.paddingTop, 0, 0
                )
            }
        }

        val glm = GridLayoutManager(context, spanCount)
        vb.rvData.layoutManager = glm
        glm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (originData[position].live == 1) {
                    spanCount
                } else {
                    1
                }
            }
        }

        val adapter = TreeAdapter<T>()
        adapter.data = originData
        adapter.setDate(requireContext(), onChickItem = {
            if (it?.isChick == false) {
                if (chickList.size < maximum) {
                    chickList.add(it)
                } else {
                    chickList[0].isChick = false
                    chickList.add(it)
                    adapter.notifyItemChanged(chickList[0].dlc_index)
                    chickList.removeAt(0)
                }

            } else {
                chickList.remove(it)
            }
        }, itemMarginEnd = itemMarginEnd, itemMarginBottom = itemMarginBottom)
        vb.rvData.adapter = adapter

        vb.tvCancel.setOnClickListener {
            dialog?.dismiss()
        }
        vb.ivCancel.setOnClickListener {
            dialog?.dismiss()
        }
        vb.tvConfirm.setOnClickListener(::confirmClick)
        vb.tvConfirmBottom.setOnClickListener(::confirmClick)

        return vb.root
    }

    private fun confirmClick(view: View) {
        if (alwaysListNotNull) {
            if (chickList.size > 0) {
                BackchickList?.invoke(chickList)
                dialog?.dismiss()
            } else {
                Toast.makeText(context, "请选择至少一个选项", Toast.LENGTH_LONG).show()
            }
        } else {
            BackchickList?.invoke(chickList)
            dialog?.dismiss()
        }
    }

    /**
     * 数据转换出来
     */
    private fun dataToItemTree(data: ArrayList<T>): ArrayList<T> {
        var newDate = ArrayList<T>()
        var index = 0
        data.forEach {
            it.live = if (isTreeArray) 1 else 2
            it.dlc_index = index
            index++
            newDate.add(it)
            if (it.toChildDlcTree() is ArrayList<*>) {
                (it.toChildDlcTree() as ArrayList<T>).forEach { two ->
                    two.live = 2
                    two.dlc_index = index
                    index++
                    newDate.add(two)
                }
            }
        }
        return newDate
    }

    override fun dismiss() {
        super.dismiss()
    }

    /**
     * 同步所以选中信息
     */
    override fun onDestroy() {
        super.onDestroy()
    }

    override fun show(manager: FragmentManager, tag: String?) {
        super.show(manager, tag)
    }

    private fun dp2px(dpValue: Float): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

}

/**
 * NORMAL正常
 * BOTTOM底部
 */
enum class DialogStyle {
    NORMAL, BOTTOM
}