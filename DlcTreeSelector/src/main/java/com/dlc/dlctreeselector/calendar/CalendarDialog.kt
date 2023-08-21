package com.dlc.dlctreeselector.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.dlc.dlctreeselector.R
import com.dlc.dlctreeselector.databinding.DialogCalendarBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

/**
 * title：
 * description：
 * author：dinglicheng on 2023/8/18 16:30
 */
class CalendarDialog : BottomSheetDialogFragment() {

    private var startTime = ""
    private var endTime = ""

    var mBackChick: ((startTime: String, endTime: String) -> Unit)? = null

    //设置时间
    var setMinTime: String? = null
    var setMaxTime: String? = null

    //最多选择日期
    var maxDate: Int = Int.MAX_VALUE

    inline fun builder(func: CalendarDialog.() -> Unit): CalendarDialog {
        this.func()
        return this
    }


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
            bottomSheetBehavior!!.maxHeight = this.resources.displayMetrics.heightPixels
            bottomSheetBehavior!!.peekHeight = this.resources.displayMetrics.heightPixels
        }
        isCancelable = false//关闭下滑
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog)
    }

    private lateinit var vb: DialogCalendarBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        vb = DialogCalendarBinding.inflate(inflater, container, false)
        Date()
        vb.tvCurrentMonth.text = SimpleDateFormat("yyyy年MM月").format(Calendar.getInstance().time)

        vb.rvData.apply {
            isMonthScrollable = false
            isItemClickable = true
            maxDate = this@CalendarDialog.maxDate
            setOnCalendarChangeListener { v, date ->
                vb.tvCurrentMonth.text = SimpleDateFormat("yyyy年MM月").format(date)
            }
            if (setMinTime?.isNotEmpty() == true) {
                setMinTime(setMinTime)
            }
            if (setMaxTime?.isNotEmpty() == true) {
                setMaxTime(setMaxTime)
            }
            setOnIntervalSelectListener { _, start, end ->
                startTime = if (start != null) SimpleDateFormat("yyyy-MM-dd").format(start) else ""
                endTime = if (end != null) SimpleDateFormat("yyyy-MM-dd").format(end) else ""
            }
        }

        // TODO: 还需要添加一个最多选7天的方法

        vb.ivLeftArrow.setOnClickListener {
            vb.rvData.lastMonth()
        }
        vb.ivRightArrow.setOnClickListener {
            vb.rvData.nextMonth()
        }

        vb.ivCancel.setOnClickListener { dismiss() }
        vb.tvConfirmBottom.setOnClickListener {
            if (startTime.isNotEmpty() && endTime.isNotEmpty()) {
                mBackChick?.invoke(startTime, endTime)
                dismiss()
            } else {
                Toast.makeText(context, "请选择完整的时间段", Toast.LENGTH_LONG).show()
            }
        }

        return vb.root
    }

}