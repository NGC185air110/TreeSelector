package com.dlc.treeselector

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.dlc.dlctreeselector.DialogStyle
import com.dlc.dlctreeselector.SelectDialog
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

        var array = ArrayList<AddressModel>()
        for (it in 0..2) {
            var arrayLeft = ArrayList<AddressModel>()
            for (index in 0..30) {
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
            dialogStyle = DialogStyle.BOTTOM
            data = array
            isTreeArray = true
            spanCount = 4
            itemMarginEnd = 15F
            itemMarginBottom = 0F
            maximum = 3
            alwaysListNotNull = false
            rvPaddingBottom = 0F
            rvPaddingStart = 15F
            rvPaddingEnd = 15F
            rvPaddingTop = 20F
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
        tvChick.setOnClickListener {
            selectDialog.show(supportFragmentManager, "selectDialog")
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