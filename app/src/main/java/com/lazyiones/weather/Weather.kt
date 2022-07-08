package com.lazyiones.weather

import android.graphics.Point

/**
 * @Description:
 * @Author: LazyIonEs
 * @Version: 1.0
 * @Date: 2022/7/7 19:44
 */
data class Weather(
    var icon: Int, //天气图标
    val temperature: Int, //温度
    val weather: String, //天气
    val time: String, //时间戳,这里为了方便使用直接用String
    var tempPoint: Point //天气坐标
)