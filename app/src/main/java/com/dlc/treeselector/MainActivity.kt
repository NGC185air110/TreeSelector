package com.dlc.treeselector

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.dlc.dlctreeselector.SelectDialog
import com.dlc.treeselector.model.AddressModel
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var textView = findViewById<TextView>(R.id.tv_text)

        var array = ArrayList<AddressModel>()
        for (it in 0..2) {
            var arrayLeft = ArrayList<AddressModel>()
            for (index in 0..100) {
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
            data = array
            isTreeArray = true
            maximum = 3
            getConfirm = {
                it.setPadding(dip2px(this@MainActivity, 12F),
                    dip2px(this@MainActivity, 7F),
                    dip2px(this@MainActivity, 12F),
                    dip2px(this@MainActivity, 7F))
                it.setBackgroundResource(R.drawable.bg_text_yellow)
            }
            BackchickList = {
                var sp = StringBuffer()
                for(date in it){
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

    }

    private fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.applicationContext.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }
}