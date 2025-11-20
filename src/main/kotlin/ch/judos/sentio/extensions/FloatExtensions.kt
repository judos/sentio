package ch.judos.sentio.extensions

import kotlin.math.ceil
import kotlin.math.floor

fun Float.ceilInt(): Int = ceil(this).toInt()
fun Float.floorInt(): Int = floor(this).toInt()
