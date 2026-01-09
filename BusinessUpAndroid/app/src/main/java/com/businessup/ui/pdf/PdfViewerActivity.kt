package com.businessup.ui.pdf

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.businessup.R
import com.businessup.databinding.ActivityPdfViewerBinding
import com.businessup.utils.toast
import java.io.File
import java.io.FileOutputStream

class PdfViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfViewerBinding
    private var pdfRenderer: PdfRenderer? = null
    private var currentPage: PdfRenderer.Page? = null
    private var pdfFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("title") ?: getString(R.string.pdf_invoice_title)
        binding.toolbar.title = title
        binding.toolbar.setNavigationOnClickListener { finish() }

        val pdfBytes = intent.getByteArrayExtra("pdf_bytes")
        if (pdfBytes != null) {
            displayPdf(pdfBytes)
            setupShareButton(pdfBytes)
        } else {
            toast(getString(R.string.pdf_error_open))
            finish()
        }
    }

    private fun displayPdf(pdfBytes: ByteArray) {
        try {
            // Save to cache file
            val cacheDir = File(cacheDir, "pdfs")
            if (!cacheDir.exists()) cacheDir.mkdirs()

            pdfFile = File(cacheDir, "invoice_${System.currentTimeMillis()}.pdf")
            FileOutputStream(pdfFile).use { it.write(pdfBytes) }

            // Open PDF renderer
            val fileDescriptor = ParcelFileDescriptor.open(
                pdfFile,
                ParcelFileDescriptor.MODE_READ_ONLY
            )
            pdfRenderer = PdfRenderer(fileDescriptor)

            // Display first page
            showPage(0)

        } catch (e: Exception) {
            e.printStackTrace()
            toast(getString(R.string.pdf_error_open))
        }
    }

    private fun showPage(index: Int) {
        pdfRenderer?.let { renderer ->
            if (index < 0 || index >= renderer.pageCount) return

            currentPage?.close()
            currentPage = renderer.openPage(index)

            currentPage?.let { page ->
                val bitmap = Bitmap.createBitmap(
                    page.width * 2,
                    page.height * 2,
                    Bitmap.Config.ARGB_8888
                )
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                binding.ivPdf.setImageBitmap(bitmap)
            }
        }
    }

    private fun setupShareButton(pdfBytes: ByteArray) {
        binding.fabShare.setOnClickListener {
            sharePdf(pdfBytes)
        }
    }

    private fun sharePdf(pdfBytes: ByteArray) {
        try {
            val cacheDir = File(cacheDir, "pdfs")
            if (!cacheDir.exists()) cacheDir.mkdirs()

            val shareFile = File(cacheDir, "factura_${System.currentTimeMillis()}.pdf")
            FileOutputStream(shareFile).use { it.write(pdfBytes) }

            val uri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                shareFile
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(Intent.createChooser(shareIntent, getString(R.string.pdf_share)))

        } catch (e: Exception) {
            e.printStackTrace()
            toast("Error al compartir")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        currentPage?.close()
        pdfRenderer?.close()
    }
}
