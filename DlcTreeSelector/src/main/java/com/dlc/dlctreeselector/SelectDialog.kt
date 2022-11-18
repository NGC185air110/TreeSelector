package com.dlc.dlctreeselector

import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import com.dlc.dlctreeselector.adapter.TreeAdapter
import com.dlc.dlctreeselector.databinding.DialogSelectBinding
import com.dlc.dlctreeselector.model.DlcTree
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

    inline fun builder(func: SelectDialog<T>.() -> Unit): SelectDialog<T> {
        this.func()
        return this
    }

    private lateinit var vb: DialogSelectBinding


    override fun onStart() {
        super.onStart()
        val dm = DisplayMetrics()
        when (maxDialogHeight) {
            0 -> {
                requireActivity().windowManager.defaultDisplay.getMetrics(dm)
                dialog!!.window!!.setLayout(dm.widthPixels, dialog!!.window!!.attributes.height)
            }
            -1 -> {

            }
            else -> {
                dialog!!.window!!.setLayout(dm.widthPixels, maxDialogHeight)
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
        val glm = GridLayoutManager(context, spanCount)
        vb.rvData.layoutManager = glm
        glm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (originData.get(position)?.live == 1) {
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
        })
        vb.rvData.adapter = adapter

        vb.tvCancel.setOnClickListener {
            dialog?.dismiss()
        }
        vb.tvConfirm.setOnClickListener {
            if (chickList.size > 0) {
                BackchickList?.invoke(chickList)
                dialog?.dismiss()
            } else {
                Toast.makeText(context, "请选择至少一个选项", Toast.LENGTH_LONG).show()
            }

        }

        return vb.root
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

}