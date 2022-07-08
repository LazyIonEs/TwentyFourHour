package com.lazyiones.weather

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat

/**
 * @Description:
 * @Author: LazyIonEs
 * @Version: 1.0
 * @Date: 2022/7/7 19:15
 */
    class TwentyFourHourView : View {

    companion object {
        private val TEMP = intArrayOf(
            21, 21, 23, 23, 23,
            22, 23, 23, 23, 22,
            21, 21, 22, 22, 23,
            23, 24, 24, 25, 25,
            25, 26, 25, 24
        )
        private val WEATHER = arrayOf(
            "晴", "晴", "多云", "多云", "多云", "雷阵雨", "小雨",
            "中雨", "中雨", "中雨", "中雨", "大雨", "大雪", "大雪",
            "大雪", "大雪", "大雪", "大雪", "大雪", "大雪", "雨夹雪",
            "雨夹雪", "雨夹雪", "雨夹雪"
        )
        private val ICON = intArrayOf(
            R.drawable.w0,
            -1,
            R.drawable.w1,
            -1,
            -1,
            R.drawable.w5,
            R.drawable.w7,
            R.drawable.w9,
            -1,
            -1,
            -1,
            R.drawable.w10,
            R.drawable.w15,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            -1,
            R.drawable.w19,
            -1,
            -1,
            -1
        )
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val itemSize = 24 //24小时
    private val itemWidth by lazy { 30.dp() } //每个Item的宽度
    private val marginLeftItem by lazy { 2.dp() } //左边预留宽度
    private val marginRightItem by lazy { 20.dp() } //右边预留宽度
    private val bottomTextHeight by lazy { 16.dp() } //底部文字高度
    private val mHeight by lazy { 140.dp() } //View的高度
    private val mWidth by lazy { marginLeftItem + marginRightItem + itemSize * itemWidth } //View的宽度
    private val tempBaseTop by lazy { (mHeight - bottomTextHeight) / 3 } //温度折线的上边Y坐标
    private val tempBaseBottom by lazy { (mHeight - bottomTextHeight) * 3 / 4 } //温度折线的下边Y坐标
    private val bitmapPaint: Paint by lazy { Paint() }
    private val linePaint: Paint by lazy { Paint() }
    private val rectPaint: Paint by lazy { Paint() }
    private val indicatorLinePaint: Paint by lazy { Paint() }
    private val textPaint: TextPaint by lazy { TextPaint() }
    private var data: ArrayList<Weather>? = null //数据集合
    private var scrollWidth: Int = 0 //IndexHorizontalScrollView的宽度
    private var maxScrollOffset: Int = 0 //滚动条最长滚动距离
    private var scrollOffset: Int = 0 //滚动条偏移量
    private var currentItemIndex: Int = 0 //当前滚动的位置所对应的item下标
    private var maxTemp: Int = 0 //最大温度
    private var minTemp: Int = 0 //最小温度

    init {
        bitmapPaint.isAntiAlias = true

        val pathEffect: PathEffect = CornerPathEffect(180f)
        linePaint.color = Color.WHITE
        linePaint.pathEffect = pathEffect
        linePaint.isAntiAlias = true
        linePaint.strokeCap = Paint.Cap.ROUND
        linePaint.strokeJoin = Paint.Join.ROUND
        linePaint.style = Paint.Style.STROKE
        linePaint.strokeWidth = 10f

        rectPaint.color = Color.parseColor("#1AFFFFFF")
        rectPaint.isAntiAlias = true
        rectPaint.style = Paint.Style.FILL
        rectPaint.strokeCap = Paint.Cap.ROUND
        rectPaint.strokeJoin = Paint.Join.ROUND
        rectPaint.strokeWidth = 1f

        textPaint.color = Color.WHITE
        textPaint.isAntiAlias = true

        indicatorLinePaint.color = Color.WHITE
        indicatorLinePaint.isAntiAlias = true
        indicatorLinePaint.strokeCap = Paint.Cap.ROUND
        indicatorLinePaint.style = Paint.Style.STROKE
        indicatorLinePaint.strokeWidth = 2f

        data = ArrayList()

        initData()
    }

    /**
     * 方便演示,自己造数据
     */
    private fun initData() {
        data?.let { data ->
            maxTemp = TEMP.max()
            minTemp = TEMP.min()
            for (i in 0 until itemSize) {
                val time = if (i < 10) {
                    "0$i:00"
                } else {
                    "$i:00"
                }
                val left = marginLeftItem + i * itemWidth
                val right = left + itemWidth - 1
                val point = calculateTempPoint(left, right, TEMP[i])
                data.add(Weather(ICON[i], TEMP[i], WEATHER[i], time, point))
            }
        }
    }


    fun setData(data: List<Weather>) {
        this.data = ArrayList()
        maxTemp = data.maxOf { weather: Weather -> weather.temperature }
        minTemp = data.minOf { weather: Weather -> weather.temperature }
        var icon = data[0].icon
        data.forEach { weather ->
            val left = marginLeftItem + data.indexOf(weather) * itemWidth
            val right = left + itemWidth - 1
            if (data.indexOf(weather) != 0 && icon == weather.icon) {
                weather.icon = -1
            } else {
                icon = weather.icon
            }
            weather.tempPoint = calculateTempPoint(left, right, weather.temperature)
        }
        this.data?.addAll(data)
        invalidate()
    }

    fun setScrollOffset(offset: Int, maxScrollOffset: Int, scrollWidth: Int) {
        this.maxScrollOffset = maxScrollOffset
        this.scrollWidth = scrollWidth
        scrollOffset = offset
        currentItemIndex = calculateItemIndex()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        onDrawLine(canvas)
        onDrawRectangle(canvas)
        onDrawIcon(canvas)
        onDrawTemp(canvas)
        onDrawText(canvas)
    }

    private fun onDrawLine(canvas: Canvas) {
        data?.let { data ->
            val path = Path()
            val point0 = data[0].tempPoint
            path.moveTo(point0.x.toFloat(), point0.y.toFloat())
            data.forEach { weather ->
                val point: Point = weather.tempPoint
                val i = data.indexOf(weather)
                if (i != 0) {
                    val pointPre: Point = data[i - 1].tempPoint
                    if (i == 1)
                        path.lineTo(point.x.toFloat(), point.y.toFloat())
                    else
                        path.rLineTo(
                            (point.x - pointPre.x).toFloat(),
                            (point.y - pointPre.y).toFloat()
                        )
                }
            }
            canvas.drawPath(path, linePaint)
        }
    }

    private fun onDrawRectangle(canvas: Canvas) {
        data?.let { data ->
            val pathBG = Path()
            val point0 = data[0].tempPoint
            pathBG.moveTo(point0.x.toFloat(), point0.y.toFloat())
            data.forEach { weather ->
                val point: Point = weather.tempPoint
                val i = data.indexOf(weather)
                if (i != 0) {
                    val pointPre: Point = data[i - 1].tempPoint
                    if (i == 1)
                        pathBG.lineTo(point.x.toFloat(), point.y.toFloat())
                    else
                        if (weather.icon != -1) pathBG.rLineTo(
                            point.x - pointPre.x - 1f.dp(), (point.y - pointPre.y).toFloat()
                        ) else pathBG.rLineTo(
                            (point.x - pointPre.x).toFloat(),
                            (point.y - pointPre.y).toFloat()
                        )

                    var pointBackup: Point = data[0].tempPoint
                    if (data[i].icon != -1 || (getGoneBehind(i) && i == data.size - 1)) {
                        for (j in 0 until i) {
                            if (data[j].icon != -1) {
                                pointBackup = data[j].tempPoint
                            }
                        }

                        rectPaint.color =
                            if (pointBackup.x < getScrollBarX() + 46f.dp() && getScrollBarX() + 46f.dp() < point.x)
                                Color.parseColor("#33FFFFFF")
                            else
                                Color.parseColor("#1AFFFFFF")

                        if (data[i].tempPoint != pointBackup) {
                            val height = mHeight - bottomTextHeight - 4f.dp() - point.y
                            pathBG.rLineTo(0f, height)
                            pathBG.rLineTo(pointBackup.x - point.x + 1f.dp(), 0f)
                            canvas.drawPath(pathBG, rectPaint)
                            pathBG.reset()
                            //移到新的点开始画
                            pathBG.moveTo(point.x.toFloat(), point.y.toFloat())
                        }
                    }
                }
            }

        }
    }

    private fun onDrawIcon(canvas: Canvas) {
        data?.let { data ->
            data.forEach { weather ->
                val point: Point = weather.tempPoint
                val i = data.indexOf(weather)
                if (i != 0) {
                    var pointBackup: Point = data[0].tempPoint
                    if (data[i].icon != -1 || (getGoneBehind(i) && i == data.size - 1)) {
                        var icon = -1
                        var indexBackUp = 0
                        for (j in 0 until i) {
                            if (data[j].icon != -1) {
                                icon = data[j].icon
                                indexBackUp = j
                                pointBackup = data[j].tempPoint
                            }
                        }
                        if (data[i].tempPoint != pointBackup) {
                            var left = (point.x - pointBackup.x) / 2 + pointBackup.x - 10.dp()
                            var right = (point.x - pointBackup.x) / 2 + pointBackup.x + 10.dp()
                            val newLeft =
                                (point.x - (pointBackup.x - getItemLeftMargin(indexBackUp))) / 2 + (pointBackup.x - getItemLeftMargin(
                                    indexBackUp
                                ))
                            val newRight =
                                ((point.x + getItemRightMargin(i)) - pointBackup.x) / 2 + pointBackup.x

                            if (getItemLeftMargin(indexBackUp) < 0 && newLeft + 20.dp() < point.x && i - indexBackUp > 1) {
                                left = newLeft - 10.dp()
                                right = left + 20.dp()
                            } else if (getItemLeftMargin(indexBackUp) < 0 && newLeft + 40.dp() >= point.x && i - indexBackUp > 1) {
                                left = point.x - 30.dp()
                                right = left + 20.dp()
                            }
                            if (getItemRightMargin(i) < 0 && newRight > pointBackup.x + 10.dp() && i - indexBackUp > 1) {
                                right = newRight + 10.dp()
                                left = right - 20.dp()
                            }

                            val drawable = ContextCompat.getDrawable(App.context, icon)
                            drawable?.let { da ->
                                da.bounds = Rect(
                                    left,
                                    tempBaseBottom + 5.dp(),
                                    right,
                                    tempBaseBottom + 25.dp()
                                )
                                da.draw(canvas)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun onDrawTemp(canvas: Canvas) {
        data?.let { data ->
            data.forEach { weather ->
                if (currentItemIndex == data.indexOf(weather)) {
                    val y = getTempBarY()
                    val targetRect =
                        Rect(getScrollBarX(), y - 40.dp(), getScrollBarX() + 92.dp(), y - 14.dp())
                    val drawable =
                        ContextCompat.getDrawable(App.context, R.drawable.bg_indicator_text)
                    drawable?.let { da ->
                        da.bounds = targetRect
                        da.draw(canvas)
                    }
                    val fontMetrics = textPaint.fontMetricsInt
                    val baseline =
                        (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2
                    textPaint.textAlign = Paint.Align.CENTER
                    textPaint.textSize = 10f.sp()
                    canvas.drawText(
                        "${weather.time} ${weather.weather} ${weather.temperature}°",
                        targetRect.centerX().toFloat(),
                        baseline.toFloat(),
                        textPaint
                    )
                    val height = mHeight - bottomTextHeight - 4f.dp()
                    canvas.drawLine(
                        targetRect.centerX().toFloat(),
                        targetRect.bottom + 4f.dp(),
                        targetRect.centerX().toFloat(),
                        height,
                        indicatorLinePaint
                    )
                }
            }
        }
    }

    private fun onDrawText(canvas: Canvas) {
        data?.let { data ->
            data.forEach { weather ->
                textPaint.textAlign = Paint.Align.CENTER
                val left = marginLeftItem + data.indexOf(weather) * itemWidth
                val right = left + itemWidth - 1
                val bottom = mHeight - bottomTextHeight
                val targetRect = Rect(left, bottom, right, bottom + bottomTextHeight)
                val fontMetrics = textPaint.fontMetricsInt
                val baseline =
                    (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2
                textPaint.textSize = 12f.sp()
                if (data.indexOf(weather) % 2 == 0) canvas.drawText(
                    weather.time,
                    (left + (itemWidth - 1) / 2).toFloat(),
                    baseline.toFloat(),
                    textPaint
                )
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(mWidth, mHeight)
    }

    private fun getGoneBehind(index: Int): Boolean {
        data?.let { data ->
            val list: ArrayList<Boolean> = ArrayList()
            for (i in index until itemSize) {
                list.add(data[i].icon == -1)
            }
            return !list.contains(false)
        }
        return false
    }

    private fun calculateTempPoint(left: Int, right: Int, temp: Int): Point {
        val minHeight = tempBaseTop.toDouble()
        val maxHeight = tempBaseBottom.toDouble()
        val tempY =
            maxHeight - (temp - minTemp) * 1.0 / (maxTemp - minTemp) * (maxHeight - minHeight)
        return Point((left + right) / 2, tempY.toInt())
    }

    private fun calculateItemIndex(): Int {
        val x = getScrollBarX()
        var sum = marginLeftItem - itemWidth
        for (i in 0 until itemSize) {
            sum += itemWidth
            if (x < sum) return i
        }
        return itemSize - 1
    }

    private fun getScrollBarX(): Int {
        val x = (itemSize - 5) * itemWidth * scrollOffset / maxScrollOffset
        return x + marginLeftItem
    }

    private fun getTempBarY(): Int {
        data?.let { data ->
            val x = getScrollBarX()
            var sum: Int = marginLeftItem
            var startPoint: Point? = null
            val endPoint: Point
            var i = 0
            for (j in 0 until itemSize) {
                sum += itemWidth
                i = j
                if (x < sum) {
                    startPoint = data[i].tempPoint
                    break
                }
            }
            startPoint ?: let {
                return data[itemSize - 1].tempPoint.y
            }
            if (i + 1 >= itemSize) return data[itemSize - 1].tempPoint.y
            endPoint = data[i + 1].tempPoint
            val left: Int = marginLeftItem + i * itemWidth
            return (startPoint.y + (x - left) * 1.0 / itemWidth * (endPoint.y - startPoint.y)).toInt()
        }
        return 0
    }

    private fun getItemLeftMargin(i: Int): Int {
        val left: Int = marginLeftItem + i * itemWidth + (itemWidth - 1) / 2
        return left - scrollOffset
    }

    private fun getItemRightMargin(i: Int): Int {
        val left: Int = marginLeftItem + i * itemWidth + (itemWidth - 1) / 2
        return scrollWidth - (left - scrollOffset)
    }
}