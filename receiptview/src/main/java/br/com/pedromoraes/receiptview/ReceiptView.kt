package br.com.pedromoraes.receiptview

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat

class ReceiptView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    private val path = Path()
    private val triangleHeight = dpToPx(10)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    init {
        setWillNotDraw(false)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        generatePath()
        if (ViewCompat.getElevation(this) > 0f && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            outlineProvider = outlineProvider
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, paint)
        canvas.clipPath(path)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun getOutlineProvider(): ViewOutlineProvider {
        return object : ViewOutlineProvider() {
            override fun getOutline(view: View?, outline: Outline?) {
                outline?.setConvexPath(path)
            }

        }
    }

    private fun generatePath() {
        val triangleAmount = 10
        val halfTriangleAmount = triangleAmount * 2
        val triangleWidth = width.toFloat() / triangleAmount
        var currentWidth = 0f
        path.reset()
        path.moveTo(0f, 0f)
        for (triangleIndex in 0 until halfTriangleAmount) {
            currentWidth = (currentWidth * 100 + triangleWidth * 100 / 2) / 100
            val currentHeight = if (triangleIndex % 2 == 0) triangleHeight else 0f
            path.lineTo(currentWidth, currentHeight)
        }
        path.lineTo(currentWidth, height.toFloat())
        for (triangleIndex in 0 until halfTriangleAmount) {
            currentWidth = (currentWidth * 100 - triangleWidth * 100 / 2) / 100
            val currentHeight =
                if (triangleIndex % 2 == 0) height.toFloat() - triangleHeight else height.toFloat()
            path.lineTo(currentWidth, currentHeight)
        }
        path.close()
    }

    private fun dpToPx(value: Int) = (value * resources.displayMetrics.density)
}