package com.example.wifiprinttest

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.print.PrintManager
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import android.widget.Toast
import java.io.IOException


class MainActivity : AppCompatActivity() {

     private lateinit var uri: Uri


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        doPrint()

    }

    private fun doPrint() {
        this.also { context ->
            // Get a PrintManager instance
            val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
            // Set job name, which will be displayed in the print queue
            val jobName = "${context.getString(R.string.app_name)} Document"
            // Start a print job, passing in a PrintDocumentAdapter implementation
            // to handle the generation of a print document



            val pdfDocument = createPdf("hello")


            printManager.print(jobName, MyPrintDocumentAdapter(pdfDocument), null)

        }
    }

    private fun createPdf(sometext: String): PdfDocument {
        // create a new document
        val document = PdfDocument()
        // crate a page description
        var pageInfo: PdfDocument.PageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
        // start a page
        var page: PdfDocument.Page = document.startPage(pageInfo)
        var canvas = page.canvas
        var paint = Paint()
        paint.color = Color.RED
        canvas.drawCircle(50f, 50f, 30f, paint)
        paint.color = Color.BLACK
        canvas.drawText(sometext, 80f, 50f, paint)

        // finish the page
        document.finishPage(page)
        // draw text on the graphics object of the page
        // Create Page 2
        pageInfo = PdfDocument.PageInfo.Builder(300, 600, 2).create()
        page = document.startPage(pageInfo)
        canvas = page.canvas
        paint = Paint()
        paint.color = Color.BLUE
        canvas.drawCircle(100f, 100f, 100f, paint)
        document.finishPage(page)
        // write the document content
        val directoryPath = Environment.getExternalStorageDirectory().path + "/mypdf/"
        val file = File(directoryPath)
        if (!file.exists()) {
            file.mkdirs()
        }
        val targetPdf = directoryPath + "test-2.pdf"
        val filePath = File(targetPdf)
        try {
            document.writeTo(FileOutputStream(filePath))
            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            Log.e("main", "error $e")
            Toast.makeText(this, "Something wrong: $e", Toast.LENGTH_LONG).show()
        }
        // close the document
        return document
    }

}
