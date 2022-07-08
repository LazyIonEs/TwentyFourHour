package com.lazyiones.weather

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.HorizontalScrollView

/**
 * @Description:
 * @Author: LazyIonEs
 * @Version: 1.0
 * @Date: 2022/7/7 19:10
 */
class IndexHorizontalScrollView :
    HorizontalScrollView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var twentyFourHourView: TwentyFourHourView? = null

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val offset = computeHorizontalScrollOffset()
        val maxOffset = computeHorizontalScrollRange() - getScreenWidth()
        twentyFourHourView?.let { twentyFourHourView ->
            twentyFourHourView.setScrollOffset(offset, maxOffset, width)
        }
    }

    fun setTwentyFourHourView(twentyFourHourView: TwentyFourHourView) {
        this.twentyFourHourView = twentyFourHourView
    }
}