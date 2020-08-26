package br.com.pedromoraes.receiptview

import android.content.res.Resources

fun Int.dpToPx(resources: Resources): Int {
    return (this * resources.displayMetrics.density).toInt()
}


fun Int.pxToDp(resources: Resources): Int {
    return (this / resources.displayMetrics.density).toInt()
}

fun Int.pxToSp(resources: Resources): Int {
    return (this / resources.displayMetrics.scaledDensity).toInt()
}

fun Int.spToPx(resources: Resources): Int {
    return (this * resources.displayMetrics.scaledDensity).toInt()
}
