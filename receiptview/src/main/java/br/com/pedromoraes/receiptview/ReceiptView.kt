package br.com.pedromoraes.receiptview

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import androidx.core.os.ConfigurationCompat
import androidx.core.view.ViewCompat
import kotlin.math.max

class ReceiptView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 14.spToPx(resources).toFloat()
    }
    private val dashPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2.dpToPx(resources).toFloat()
        pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
    }
    private val path = Path()
    private val triangleHeight = 30f
    private var iconBitmap: Bitmap? = null
    private var currentDrawY: Float = 0f
    private val startMargin = 16.dpToPx(resources)
    private val textStartX = startMargin.toFloat()
    private val endMargin = 16.dpToPx(resources)
    private val textHeight = textPaint.fontMetrics.descent - textPaint.fontMetrics.ascent

    var entries = listOf(
        SingleText("Bar do Pedrinho"),
        LineSeparator,
        LabeledData("Data", "14/08/2020"),
        LabeledData("Atendente", "Pedro Moraes"),
        LineSeparator,
        ProductInfo("Coca Cola 350ml", 7, 3.57),
        ProductInfo("Heineken 600ml", 17, 7.80),
        ProductInfo("Batata Frita", 2, 14.0),
        LineSeparator
    )

    init {
        setWillNotDraw(false)
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.ReceiptView)
            val icon = typedArray.getDrawable(R.styleable.ReceiptView_icon)
            icon?.let { createIconBitmap(icon) }
            typedArray.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var height = triangleHeight * 2 + textHeight + 48.dpToPx(resources)
        iconBitmap?.let { height += it.height }
        entries.forEach {
            val entryHeight: Float = when (it) {
                is SingleText -> textHeight + 8.dpToPx(resources)
                is LabeledData -> textHeight + 8.dpToPx(resources)
                is LineSeparator -> 26.dpToPx(resources).toFloat()
                is ProductInfo -> textHeight * 2 + 8.dpToPx(resources)
            }
            height += entryHeight
        }
        setMeasuredDimension(suggestedMinimumWidth, height.toInt())
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        generatePath()
        if (ViewCompat.getElevation(this) > 0f && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            outlineProvider = outlineProvider
    }

    private fun createIconBitmap(icon: Drawable) {
        val maxSize = max(icon.intrinsicWidth, icon.intrinsicHeight)
        val ratio = icon.intrinsicHeight / 64.dpToPx(resources).toFloat()
        val iconWidth = icon.intrinsicWidth / ratio
        val iconHeight = icon.intrinsicHeight / ratio
        iconBitmap = Bitmap.createBitmap(
            iconWidth.toInt(), iconHeight.toInt(), Bitmap.Config.ARGB_8888
        ).apply {
            val canvas = Canvas(this)
            icon.setBounds(0, 0, canvas.width, canvas.height)
            icon.draw(canvas)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, paint)
        currentDrawY = triangleHeight + 20.dpToPx(resources)
        iconBitmap?.let {
            canvas.drawBitmap(it, (width - it.width) / 2f, currentDrawY, null)
            currentDrawY += it.height
        }

        entries.forEach { receiptItem ->
            when (receiptItem) {
                is SingleText -> drawSingleText(receiptItem, canvas)
                is LabeledData -> drawLabeledData(receiptItem, canvas)
                is LineSeparator -> drawLineSeparator(canvas)
                is ProductInfo -> drawProductInfo(receiptItem, canvas)
            }
        }
        drawTotalValue(canvas)
    }

    private fun generatePath() {
        val triangleWidth = width / 10f
        var currentWidth = 0f
        path.reset()
        path.moveTo(0f, 0f)
        for (triangleIndex in 0..19) {
            currentWidth = (currentWidth * 100 + triangleWidth * 100 / 2) / 100
            val currentHeight = if (triangleIndex % 2 == 0) triangleHeight else 0f
            path.lineTo(currentWidth, currentHeight)
        }
        path.lineTo(currentWidth, height.toFloat())
        for (triangleIndex in 0..19) {
            currentWidth = (currentWidth * 100 - triangleWidth * 100 / 2) / 100
            val currentHeight =
                if (triangleIndex % 2 == 0) height.toFloat() - triangleHeight else height.toFloat()
            path.lineTo(currentWidth, currentHeight)
        }
        path.close()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun getOutlineProvider(): ViewOutlineProvider {
        return object : ViewOutlineProvider() {
            override fun getOutline(view: View?, outline: Outline?) {
                outline?.setConvexPath(path)
            }
        }
    }

    private fun drawLabeledData(receiptItem: LabeledData, canvas: Canvas) {
        val topMargin = 8.dpToPx(resources)
        currentDrawY += topMargin + textHeight
        val label = "${receiptItem.label}:"
        val value = receiptItem.value
        val valueWidth = textPaint.measureText(value)
        canvas.drawText(label, textStartX, currentDrawY, textPaint)
        val textEndX = width - endMargin
        canvas.drawText(value, textEndX - valueWidth, currentDrawY, textPaint)
    }

    private fun drawSingleText(receiptItem: SingleText, canvas: Canvas) {
        currentDrawY += 8.dpToPx(resources) + textHeight
        val textWidth = textPaint.measureText(receiptItem.value)
        canvas.drawText(receiptItem.value, (width - textWidth) / 2, currentDrawY, textPaint)
    }

    private fun drawLineSeparator(canvas: Canvas) {
        val separatorTopMargin = 20.dpToPx(resources).toFloat()
        val separatorStartX = 14.dpToPx(resources).toFloat()
        val separatorEndX = width.toFloat() - 14.dpToPx(resources)
        currentDrawY += separatorTopMargin
        canvas.drawLine(separatorStartX, currentDrawY, separatorEndX, currentDrawY, dashPaint)
        currentDrawY += 4.dpToPx(resources)
    }

    private fun drawProductInfo(receiptItem: ProductInfo, canvas: Canvas) {
        val textEndX = width - endMargin
        currentDrawY += 8.dpToPx(resources) + textHeight
        val locale = ConfigurationCompat.getLocales(resources.configuration)[0]
        val totalPrice =
            "R$ " + String.format(locale, "%.2f", receiptItem.amount * receiptItem.unitPrice)
        val totalPriceWidth = textPaint.measureText(totalPrice)
        canvas.drawText(receiptItem.description, textStartX, currentDrawY, textPaint)
        currentDrawY += textHeight
        val formattedUnitPrice = String.format(locale, "%.2f", receiptItem.unitPrice)
        canvas.drawText(
            "${receiptItem.amount}x R$ $formattedUnitPrice",
            textStartX + 4.dpToPx(resources),
            currentDrawY,
            textPaint
        )

        canvas.drawText(totalPrice, textEndX - totalPriceWidth, currentDrawY, textPaint)
    }


    private fun drawTotalValue(canvas: Canvas) {
        val locale = ConfigurationCompat.getLocales(resources.configuration)[0]
        currentDrawY += textHeight + 8.dpToPx(resources)
        val totalValue =
            entries.filterIsInstance<ProductInfo>().sumByDouble { it.unitPrice * it.amount }
        val totalText = "Total: R$ ${String.format(locale, "%.2f", totalValue)}"
        val totalTextWidth = textPaint.measureText(totalText)
        val textEndX = width - endMargin
        canvas.drawText(totalText, textEndX - totalTextWidth, currentDrawY, textPaint)
    }
}