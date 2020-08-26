package br.com.pedromoraes.receiptview

sealed class ReceiptItem

object LineSeparator : ReceiptItem()

data class LabeledData(val label: String, val value: String) : ReceiptItem()

data class SingleText(val value: String) : ReceiptItem()

data class ProductInfo(val description: String, val amount: Int, val unitPrice: Double) : ReceiptItem()
