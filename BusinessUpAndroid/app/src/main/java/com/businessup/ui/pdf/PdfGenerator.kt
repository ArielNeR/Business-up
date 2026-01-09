package com.businessup.ui.pdf

import android.content.Context
import com.businessup.data.model.CarritoItem
import com.businessup.data.model.Cliente
import com.businessup.data.model.Usuario
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PdfGenerator(private val context: Context) {

    private val primaryColor = DeviceRgb(111, 207, 151)
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    fun generateInvoice(
        numeroFactura: Int,
        cliente: Cliente,
        items: List<CarritoItem>,
        total: Double,
        fecha: Long,
        metodoPago: String,
        usuario: Usuario?
    ): ByteArray {
        val outputStream = ByteArrayOutputStream()

        val pdfWriter = PdfWriter(outputStream)
        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument, PageSize.A4)
        document.setMargins(40f, 40f, 40f, 40f)

        // Header
        addHeader(document, usuario)

        // Invoice info
        addInvoiceInfo(document, numeroFactura, fecha, metodoPago)

        // Client info
        addClientInfo(document, cliente)

        // Items table
        addItemsTable(document, items)

        // Total
        addTotal(document, total)

        // Footer
        addFooter(document)

        document.close()
        return outputStream.toByteArray()
    }

    private fun addHeader(document: Document, usuario: Usuario?) {
        val header = Paragraph("BUSINESS UP")
            .setFontSize(28f)
            .setBold()
            .setFontColor(primaryColor)
            .setTextAlignment(TextAlignment.CENTER)
        document.add(header)

        val subtitle = Paragraph("FACTURA DE VENTA")
            .setFontSize(16f)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(20f)
        document.add(subtitle)

        usuario?.let {
            val vendedor = Paragraph("Vendedor: ${it.nombre}")
                .setFontSize(10f)
                .setTextAlignment(TextAlignment.RIGHT)
            document.add(vendedor)
        }
    }

    private fun addInvoiceInfo(document: Document, numeroFactura: Int, fecha: Long, metodoPago: String) {
        val table = Table(UnitValue.createPercentArray(floatArrayOf(1f, 1f)))
            .useAllAvailableWidth()
            .setMarginTop(20f)
            .setMarginBottom(20f)

        // Left column
        val leftCell = Cell()
            .setBorder(Border.NO_BORDER)
            .add(Paragraph("Factura #: $numeroFactura").setBold())
            .add(Paragraph("Fecha: ${dateFormat.format(Date(fecha))}"))

        // Right column
        val rightCell = Cell()
            .setBorder(Border.NO_BORDER)
            .setTextAlignment(TextAlignment.RIGHT)
            .add(Paragraph("Método de pago: $metodoPago"))

        table.addCell(leftCell)
        table.addCell(rightCell)
        document.add(table)
    }

    private fun addClientInfo(document: Document, cliente: Cliente) {
        val clientSection = Paragraph("DATOS DEL CLIENTE")
            .setFontSize(12f)
            .setBold()
            .setFontColor(primaryColor)
            .setMarginTop(10f)
        document.add(clientSection)

        val clientTable = Table(UnitValue.createPercentArray(floatArrayOf(1f, 2f)))
            .useAllAvailableWidth()
            .setMarginBottom(20f)

        clientTable.addCell(createInfoCell("Nombre:"))
        clientTable.addCell(createValueCell(cliente.nombre))

        clientTable.addCell(createInfoCell("ID:"))
        clientTable.addCell(createValueCell(cliente.idCliente))

        if (cliente.numerosContacto.isNotEmpty()) {
            clientTable.addCell(createInfoCell("Teléfono:"))
            clientTable.addCell(createValueCell(cliente.numerosContacto.first()))
        }

        if (cliente.correos.isNotEmpty()) {
            clientTable.addCell(createInfoCell("Email:"))
            clientTable.addCell(createValueCell(cliente.correos.first()))
        }

        document.add(clientTable)
    }

    private fun addItemsTable(document: Document, items: List<CarritoItem>) {
        val itemsSection = Paragraph("DETALLE DE PRODUCTOS/SERVICIOS")
            .setFontSize(12f)
            .setBold()
            .setFontColor(primaryColor)
            .setMarginTop(10f)
        document.add(itemsSection)

        val table = Table(UnitValue.createPercentArray(floatArrayOf(3f, 1f, 1f, 1f)))
            .useAllAvailableWidth()
            .setMarginTop(10f)

        // Header row
        table.addHeaderCell(createHeaderCell("Descripción"))
        table.addHeaderCell(createHeaderCell("Cantidad"))
        table.addHeaderCell(createHeaderCell("Precio Unit."))
        table.addHeaderCell(createHeaderCell("Subtotal"))

        // Items
        for (item in items) {
            table.addCell(createTableCell(item.nombre))
            table.addCell(createTableCell(item.cantidad.toString(), TextAlignment.CENTER))
            table.addCell(createTableCell("$${String.format("%.2f", item.precio)}", TextAlignment.RIGHT))
            table.addCell(createTableCell(item.datoSubtotal, TextAlignment.RIGHT))
        }

        document.add(table)
    }

    private fun addTotal(document: Document, total: Double) {
        val totalTable = Table(UnitValue.createPercentArray(floatArrayOf(3f, 1f)))
            .useAllAvailableWidth()
            .setMarginTop(20f)

        val emptyCell = Cell().setBorder(Border.NO_BORDER)
        val totalCell = Cell()
            .setBackgroundColor(primaryColor)
            .setPadding(10f)
            .add(
                Paragraph("TOTAL: $${String.format("%.2f", total)}")
                    .setBold()
                    .setFontSize(16f)
                    .setFontColor(ColorConstants.WHITE)
                    .setTextAlignment(TextAlignment.RIGHT)
            )

        totalTable.addCell(emptyCell)
        totalTable.addCell(totalCell)
        document.add(totalTable)
    }

    private fun addFooter(document: Document) {
        val footer = Paragraph("¡Gracias por su compra!")
            .setFontSize(12f)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(40f)
            .setFontColor(primaryColor)
        document.add(footer)

        val appName = Paragraph("Generado con Business Up")
            .setFontSize(8f)
            .setTextAlignment(TextAlignment.CENTER)
            .setFontColor(ColorConstants.GRAY)
        document.add(appName)
    }

    private fun createHeaderCell(text: String): Cell {
        return Cell()
            .setBackgroundColor(primaryColor)
            .setPadding(8f)
            .add(
                Paragraph(text)
                    .setBold()
                    .setFontColor(ColorConstants.WHITE)
                    .setFontSize(10f)
            )
    }

    private fun createTableCell(text: String, alignment: TextAlignment = TextAlignment.LEFT): Cell {
        return Cell()
            .setPadding(8f)
            .add(
                Paragraph(text)
                    .setFontSize(10f)
                    .setTextAlignment(alignment)
            )
    }

    private fun createInfoCell(text: String): Cell {
        return Cell()
            .setBorder(Border.NO_BORDER)
            .add(Paragraph(text).setBold().setFontSize(10f))
    }

    private fun createValueCell(text: String): Cell {
        return Cell()
            .setBorder(Border.NO_BORDER)
            .add(Paragraph(text).setFontSize(10f))
    }
}
