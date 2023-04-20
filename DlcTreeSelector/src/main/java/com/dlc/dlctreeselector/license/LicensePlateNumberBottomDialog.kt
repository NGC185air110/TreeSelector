package com.dlc.dlctreeselector.license

import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dlc.dlctreeselector.R
import com.dlc.dlctreeselector.adapter.LicensePlateNumberAdapter
import com.dlc.dlctreeselector.databinding.DialogLicensePlateNumberBottomBinding
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * title：
 * description：底部弹出车牌输入框
 * author：dinglicheng on 2023/4/20 15:04
 */
class LicensePlateNumberBottomDialog : BottomSheetDialogFragment() {
    private lateinit var vb: DialogLicensePlateNumberBottomBinding

    var mLicensePlateNumberAdapter = LicensePlateNumberAdapter()

    var dataArray: ArrayList<String> = arrayListOf()

    //挂车模式后面少个框
    var isTrailer: Boolean = false

    inline fun builder(func: LicensePlateNumberBottomDialog.() -> Unit): LicensePlateNumberBottomDialog {
        this.func()
        return this
    }

    //返回选中数值
    var backString: ((String) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        vb = DialogLicensePlateNumberBottomBinding.inflate(inflater, container, false)
        vb.ivCancel.setOnClickListener { dismiss() }

        if (isTrailer) {
            vb.etCarNo.mFigures = 6
            vb.tvTrailer.visibility = View.VISIBLE
        } else {
            vb.etCarNo.mFigures = 7
            vb.tvTrailer.visibility = View.GONE
        }


        vb.rvData.layoutManager = FlexboxLayoutManager(context).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
            justifyContent = JustifyContent.FLEX_START
        }
        vb.rvData.addItemDecoration(SpacesItemDecoration(dp2px(10F)))
        arraySetRegion()
        mLicensePlateNumberAdapter.setDate(dataArray, requireContext()) {
            vb.etCarNo.text?.append(it)
        }
        vb.rvData.adapter = mLicensePlateNumberAdapter

        vb.ivBackspace.setOnClickListener {
            if ((vb.etCarNo.text?.length ?: 0) > 0) {
                val index = vb.etCarNo.selectionStart
                vb.etCarNo.text?.delete(index - 1, index)
            }

        }

        vb.etCarNo.onVerifyCodeChangedListener =
            object : VerifyCodeEditText.OnVerifyCodeChangedListener {
                override fun onVerCodeChanged(
                    s: CharSequence,
                    start: Int,
                    before: Int,
                    count: Int,
                ) {
                    when (s.length) {
                        0 -> {
                            arraySetRegion()
                            mLicensePlateNumberAdapter.notifyDataSetChanged()
                        }
                        1 -> {
                            arraySetCity()
                            mLicensePlateNumberAdapter.notifyDataSetChanged()
                        }
                        else -> {
                            arraySetNumb()
                            mLicensePlateNumberAdapter.notifyDataSetChanged()
                        }
                    }
                }

                override fun onInputCompleted(s: CharSequence) {
                }

            }

        vb.tvConfirmBottom.setOnClickListener {
            if (isTrailer) {
                backString?.invoke("${vb.etCarNo.text.toString()}挂")
            }else{
                backString?.invoke(vb.etCarNo.text.toString())
            }
            dismiss()
        }

        return vb.root
    }

    inner class SpacesItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State,
        ) {
            if ((parent.getChildAdapterPosition(view) + 1) % 7 == 0) {
                outRect.right = 0 //第一列左边贴边
            } else {
                outRect.right = space
            }
            outRect.bottom = space
        }
    }

    private fun arraySetRegion() {
        dataArray.clear()
        dataArray.addAll(
            arrayListOf(
                "京",
                "津",
                "沪",
                "渝",
                "冀",
                "豫",
                "云",
                "辽",
                "黑",
                "湘",
                "皖",
                "鲁",
                "新",
                "苏",
                "浙",
                "赣",
                "鄂",
                "桂",
                "甘",
                "晋",
                "蒙",
                "陕",
                "吉",
                "闽",
                "贵",
                "粤",
                "青",
                "藏",
                "川",
                "宁",
                "琼"
            )
        )
    }

    private fun arraySetCity() {
        dataArray.clear()
        dataArray.addAll(
            arrayListOf(
                "A",
                "B",
                "C",
                "D",
                "E",
                "F",
                "G",
                "H",
                "J",
                "K",
                "L",
                "M",
                "N",
                "P",
                "Q",
                "R",
                "S",
                "T",
                "U",
                "V",
                "W",
                "X",
                "Y",
                "Z",
            )
        )
    }

    private fun arraySetNumb() {
        dataArray.clear()
        dataArray.addAll(
            arrayListOf(
                "1",
                "2",
                "3",
                "4",
                "5",
                "6",
                "7",
                "8",
                "9",
                "0",
                "A",
                "B",
                "C",
                "D",
                "E",
                "F",
                "G",
                "H",
                "J",
                "K",
                "L",
                "M",
                "N",
                "P",
                "Q",
                "R",
                "S",
                "T",
                "U",
                "V",
                "W",
                "X",
                "Y",
                "Z",
            )
        )
    }

    private fun dp2px(dpValue: Float): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }
}