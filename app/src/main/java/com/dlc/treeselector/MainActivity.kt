package com.dlc.treeselector

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.dlc.dlctreeselector.DialogStyle
import com.dlc.dlctreeselector.SelectDialog
import com.dlc.dlctreeselector.calendar.CalendarDialog
import com.dlc.dlctreeselector.license.LicensePlateNumberBottomDialog
import com.dlc.treeselector.model.AddressModel
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var textView = findViewById<TextView>(R.id.tv_text)
        var tvChick = findViewById<TextView>(R.id.tv_chick)
        var tvCarNo = findViewById<TextView>(R.id.tv_car_no)
        var tvCalendar = findViewById<TextView>(R.id.tv_calendar)

        var array = ArrayList<AddressModel>()
        for (it in 0..2) {
            var arrayLeft = ArrayList<AddressModel>()
            for (index in 0..10) {
                arrayLeft.add(AddressModel().apply {
                    id = Random.nextInt()
                    name = "$it+$index"
                })
            }
            array.add(AddressModel().apply {
                id = Random.nextInt()
                name = "$it"
                data = arrayLeft
            })
        }


        var selectDialog = SelectDialog<AddressModel>().builder {
            maxDialogHeight = 550
            selectCancelable = true
            dialogStyle = DialogStyle.BOTTOM
            data = array
            isTreeArray = true
            mutuallyExclusive = true
            mutuallyExclusiveToastValue = "不能同时选择罐车和非罐车"
            spanCount = 3
            itemMarginEnd = 15F
            itemMarginBottom = 10F
            maximum = 10000000
            alwaysListNotNull = false
            rvPaddingBottom = 0F
            rvPaddingStart = 0F
            rvPaddingEnd = 0F
            rvPaddingTop = 0F
            getConfirm = {
                it.setPadding(
                    dip2px(this@MainActivity, 12F),
                    dip2px(this@MainActivity, 7F),
                    dip2px(this@MainActivity, 12F),
                    dip2px(this@MainActivity, 7F)
                )
                it.setBackgroundResource(R.drawable.bg_text_yellow)
            }
            BackchickList = {
                var sp = StringBuffer()
                for (date in it) {
                    sp.append(date.name)
                    sp.append(",")
                    sp.append(date.id)
                    sp.append("  ")
                }
                textView.text = sp.toString()
            }
        }

        textView.setOnClickListener {
            selectDialog.show(supportFragmentManager, "selectDialog")
        }

        var selectDialogRv = SelectDialog<AddressModel>().builder {
            dialogStyle = DialogStyle.BOTTOM
            data = array
            maximum = 3
            isTreeArray = false
            itemMarginBottom = 10F
            itemMarginEnd = 0F
            spanCount = 1
            rvCornerManager = 15
            tvColorOff = R.color.teal_200
            tvColorOn = R.color.black
            selectBold = true
            BackchickList = {

            }
        }

        tvChick.setOnClickListener {
            selectDialogRv.show(supportFragmentManager, "selectDialog")
        }
        tvCalendar.setOnClickListener {
            CalendarDialog().builder {
                setMinTime = "2023-05-01"
                setMaxTime = "2023-08-18"
                maxDate = 30
                mBackChick = { startTime, endTime ->

                }
            }.show(supportFragmentManager, "CalendarDialog")
        }

        val objectAnimation = ObjectAnimator.ofFloat(tvChick, "scaleY", 1f, 2f)
        objectAnimation.duration = 1000
        objectAnimation.repeatCount = 2
        objectAnimation.repeatMode = ValueAnimator.REVERSE
        objectAnimation.repeatCount = ValueAnimator.INFINITE
        objectAnimation.start()


        var carDialog = LicensePlateNumberBottomDialog().builder {
            isTrailer = true
            backString = {
                tvCarNo.text = it
            }
        }
        tvCarNo.setOnClickListener {
            carDialog.show(supportFragmentManager, "carDialog")
        }

    }

    private fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.applicationContext.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }
}