package com.dlc.dlctreeselector.license

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatEditText
import com.dlc.dlctreeselector.R
import java.util.*


/**
 * https://github.com/LingChenJie/VerifyCodeView
 * 在原基础修改
 * 自定义验证码输入框
 */
class VerifyCodeEditText @JvmOverloads constructor(
    context: Context,
    private var attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AppCompatEditText(context, attrs, defStyleAttr) {


    var mFigures = 0// 验证码个数
    private var mCodeMargin = 5// 验证码之间的间距
    private var mSelectColor = 0// 选中框的颜色
    private var mNormalColor = 0// 普通框的颜色
    private var mBorderRadius = 0f// 边框直角的曲度
    private var mBorderWidth = 0f// 边框的厚度
    private var mCursorWidth = 0f// 光标宽度
    private var mCursorColor = 0// 光标的颜色
    private var mCursorDuration = 0L// 光标闪烁的时间
    private var mItemRectLength = 40//单位DP
    private var mSeparatorDisplay = false//是否显示分割符
    private var mSeparatorDisplayWidth = 20F//2-3间距


    var onVerifyCodeChangedListener: OnVerifyCodeChangedListener? = null
    private var mCurrentPosition = 0// 当前验证码的位置
    private var mEachRectLength = 0// 矩形边长
    private val mNormalPaint = Paint()
    private val mSelectPaint = Paint()
    private val mCursorPaint = Paint()
    private val mSeparatorPaint = Paint()//分割点的画笔

    // 控制光标闪烁
    var isCursorShowing = false
    var mCursorTimerTask: TimerTask? = null
    var mCursorTimer: Timer? = null

    init {
        initAttr()
        initPaint()
        initCursorTimer()
        isFocusableInTouchMode = true
        initTextChangedListener()
    }

    private fun initAttr() {
        val ta = context.obtainStyledAttributes(attrs!!, R.styleable.VerifyCodeEditText)
        mFigures = ta.getInteger(R.styleable.VerifyCodeEditText_figures, 7)
        mCodeMargin = ta.getDimension(R.styleable.VerifyCodeEditText_codeMargin, 0f).toInt()
        mSelectColor =
            ta.getColor(R.styleable.VerifyCodeEditText_selectBorderColor, currentTextColor)
        mNormalColor =
            ta.getColor(
                R.styleable.VerifyCodeEditText_normalBorderColor,
                resources.getColor(android.R.color.darker_gray)
            )
        mBorderRadius = ta.getDimension(R.styleable.VerifyCodeEditText_borderRadius, 6f)
        mBorderWidth = ta.getDimension(R.styleable.VerifyCodeEditText_borderWidth, 1f)
        mCursorWidth = ta.getDimension(R.styleable.VerifyCodeEditText_cursorWidth, 1f)
        mCursorColor =
            ta.getColor(
                R.styleable.VerifyCodeEditText_cursorColor,
                resources.getColor(android.R.color.darker_gray)
            )
        mCursorDuration =
            ta.getInteger(R.styleable.VerifyCodeEditText_cursorDuration, DEFAULT_CURSOR_DURATION)
                .toLong()

        mItemRectLength = ta.getInteger(R.styleable.VerifyCodeEditText_itemRectLength, 6)
        mSeparatorDisplay = ta.getBoolean(R.styleable.VerifyCodeEditText_separatorDisplay, false)
        ta.recycle()

        // force LTR because of bug: https://github.com/JustKiddingBaby/VercodeEditText/issues/4
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            layoutDirection = LAYOUT_DIRECTION_LTR
        }
    }

    private fun initPaint() {
        mNormalPaint.isAntiAlias = true
        mNormalPaint.color = mNormalColor
        mNormalPaint.style = Paint.Style.STROKE// 空心
        mNormalPaint.strokeWidth = mBorderWidth

        mSelectPaint.isAntiAlias = true
        mSelectPaint.color = mSelectColor
        mSelectPaint.style = Paint.Style.STROKE// 空心
        mSelectPaint.strokeWidth = mBorderWidth

        mCursorPaint.isAntiAlias = true
        mCursorPaint.color = mCursorColor
        mCursorPaint.style = Paint.Style.FILL_AND_STROKE
        mCursorPaint.strokeWidth = mCursorWidth

        mSeparatorPaint.apply {
            isAntiAlias = true
            color = Color.parseColor("#444444")
            strokeWidth = 5F
            style = Paint.Style.FILL//实心
        }
    }

    private fun initCursorTimer() {
        mCursorTimerTask = object : TimerTask() {
            override fun run() {
                // 通过光标间歇性显示实现闪烁效果
                isCursorShowing = !isCursorShowing
                postInvalidate()
            }
        }
        mCursorTimer = Timer()
    }

    private fun initTextChangedListener() {
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                mCurrentPosition = text!!.length
                postInvalidate()
                if (text!!.length == mFigures) {
                    onVerifyCodeChangedListener?.onInputCompleted(text!!)
                } else if (text!!.length > mFigures) {
                    text!!.delete(mFigures, text!!.length)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                mCurrentPosition = text!!.length
                postInvalidate()
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                mCurrentPosition = text!!.length
                postInvalidate()
                onVerifyCodeChangedListener?.onVerCodeChanged(text!!, start, before, count)
            }

        })
    }

    //测量总体宽高
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthResult = 0
        var heightResult = 0

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        //非自适应宽度的时候用的获取到具体宽度
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        widthResult = if (widthMode == MeasureSpec.EXACTLY) {
            widthSize
        } else {
            getScreenWidth(context)
        }
        // 每个矩形的宽度
        mEachRectLength = dp2px(mItemRectLength.toFloat())

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        heightResult = if (heightMode == MeasureSpec.EXACTLY) {
            heightSize
        } else {
            mEachRectLength
        }

        setMeasuredDimension(widthResult, heightResult)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        /*event?.apply {
            if (action == MotionEvent.ACTION_DOWN) {
                requestFocus()
                setSelection(text!!.length)
                showKeyBoard(context)
                return false
            }
        }*/

        return false
    }

    //绘制具体item内容
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        mCurrentPosition = text!!.length
        val width = mEachRectLength - paddingLeft - paddingRight
        val height = measuredHeight - paddingTop - paddingBottom
        for (i in 0 until mFigures) {
            canvas.save()

            var start = width * i + mCodeMargin * i + mBorderWidth
            //计算分割点距离
            if (i > 1 && mSeparatorDisplay) {
                //中间的分割点
                start += dp2px(mSeparatorDisplayWidth)
            }
            var end = start + width - mBorderWidth
            if (i == mFigures - 1) {
                end -= mBorderWidth
            }
            // 画矩形选框
            val rect = RectF(start, mBorderWidth, end, height.toFloat() - mBorderWidth)
            //Log.d("TAGGG", "当前位置圆角位置 $start $mBorderWidth $end ${height.toFloat() - mBorderWidth}")
            if (i == mCurrentPosition) {//选中的下一个状态
                canvas.drawRoundRect(rect, mBorderRadius, mBorderRadius, mSelectPaint)
            } else {
                canvas.drawRoundRect(rect, mBorderRadius, mBorderRadius, mNormalPaint)
            }
            canvas.restore()
        }

        // 绘制文字
        val value = text.toString()
        for (i in value.indices) {
            canvas.save()
            val start = width * i + mCodeMargin * i
            var x = start + width / 2f// x
            //计算文字
            if (i > 1 && mSeparatorDisplay) {
                //中间的分割点
                x += dp2px(mSeparatorDisplayWidth)
            }
            paint.textAlign = Paint.Align.CENTER
            paint.color = currentTextColor
            val fontMetrics = paint.fontMetrics
            val baseline = (height - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top
            canvas.drawText(value[i].toString(), x, baseline, paint)
            canvas.restore()
        }

        // 绘制光标
        if (!isCursorShowing && isCursorVisible && mCurrentPosition < mFigures && hasFocus()) {
            canvas.save()
            var startX = (width + mCodeMargin) * mCurrentPosition + width / 2f
            if (mCurrentPosition > 1 && mSeparatorDisplay) {
                startX += dp2px(mSeparatorDisplayWidth)
            }
            val startY = height / 4f
            val endX = startX
            val endY = height - height / 4f
            canvas.drawLine(startX, startY, endX, endY, mCursorPaint)
            canvas.restore()
        }

        //绘制2-3中间的分割点
        if (mSeparatorDisplay) {
            canvas.save()
            val separatorX =
                (width * 2 + mCodeMargin * 2 + mBorderWidth) + (dp2px(mSeparatorDisplayWidth - 5F) / 2)
            val separatorY = height / 2F
            canvas.drawCircle(separatorX, separatorY, 5F, mSeparatorPaint)
            canvas.restore()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // 启动定时任务，定时刷新实现光标闪烁
        mCursorTimer?.scheduleAtFixedRate(mCursorTimerTask, 0, mCursorDuration)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mCursorTimer?.cancel()
        mCursorTimer = null
    }

    private fun getScreenWidth(context: Context?): Int {
        val metrics = DisplayMetrics()
        val wm = context!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(metrics)
        return metrics.widthPixels
    }

    fun showKeyBoard(context: Context) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_FORCED)
    }

    companion object {
        val DEFAULT_CURSOR_DURATION = 400//光标闪烁的默认间隔时间
    }

    /**
     * 验证码变化时候的监听事件
     */
    interface OnVerifyCodeChangedListener {
        /**
         * 当验证码变化的时候
         */
        fun onVerCodeChanged(s: CharSequence, start: Int, before: Int, count: Int)

        /**
         * 输入完毕后的回调
         */
        fun onInputCompleted(s: CharSequence)
    }

    private fun dp2px(dpValue: Float): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

}
