package com.lazyiones.weather

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

/**
 * dp 转 px
 */
fun dp2px(dp: Float): Float = dp * App.context.resources.displayMetrics.density

fun Float.dp() = dp2px(this)

fun Int.dp() = dp2px(this.toFloat()).toInt()

/**
 * sp 转 px
 */
fun sp2px(sp: Float): Float = sp * App.context.resources.displayMetrics.scaledDensity

fun Float.sp() = sp2px(this)

fun Int.sp() = sp2px(this.toFloat()).toInt()

/**
 * 获取屏幕宽度
 */
fun getScreenWidth() = App.context.resources.displayMetrics.widthPixels

/**
 * 根据时间戳获取时间
 */
@SuppressLint("SimpleDateFormat")
fun getDateHHmm(data: Long) = SimpleDateFormat("HH:mm").format(Date(data))
