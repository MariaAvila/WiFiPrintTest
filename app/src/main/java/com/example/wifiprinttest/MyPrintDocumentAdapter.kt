package com.example.wifiprinttest

import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.graphics.pdf.PdfDocument
import java.io.*


class MyPrintDocumentAdapter(private val pdfDocument : PdfDocument) : PrintDocumentAdapter() {


    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes?,
        cancellationSignal: CancellationSignal?,
        callback: LayoutResultCallback?,
        extras: Bundle?
    ) {

        // Respond to cancellation request
        if (cancellationSignal?.isCanceled == true) {
            callback?.onLayoutCancelled()
            return
        }

        // Compute the expected number of printed pages
        //val pages = computePageCount(newAttributes)

        val pages = pdfDocument.pages.size

        if (pages > 0) {
            // Return print information to print framework
            PrintDocumentInfo.Builder("print_output.pdf")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(pages)
                .build()
                .also { info ->
                    // Content layout reflow is complete
                    callback?.onLayoutFinished(info, true)
                }
        } else {
            // Otherwise report an error to the print framework
            callback?.onLayoutFailed("Page count calculation failed.")
        }
    }


    private fun containsPage(pages: Array<PageRange>, i: Int): Boolean {
        // check if contains
        return true
    }

    override fun onWrite(
        pageRanges: Array<PageRange>, destination: ParcelFileDescriptor,
        cancellationSignal: CancellationSignal, callback: WriteResultCallback
    ) {
        // iterate over every page of the document.
        // check if it's in the output range
        val totalPages = pdfDocument.pages.size
        for (i in 0 until totalPages) {
            // Check to see if this page is in the output range.
            if (containsPage(pageRanges, i)) {
                val writtenPagesArray = pdfDocument.pages
                // If so, add it to writtenPagesArray. writtenPagesArray.size()
                // is used to compute the next output page index.
                //writtenPagesArray.append(writtenPagesArray.size(), i);
                val pageInfo = writtenPagesArray[i]
                val page = pdfDocument.startPage(pageInfo)

                // check for cancellation
                if (cancellationSignal.isCanceled) {
                    callback.onWriteCancelled()
                    pdfDocument.close()
                    return
                }

                // Rendering is complete, so page can be finalized.
                pdfDocument.finishPage(page)
            }
        }

        try {
            pdfDocument.writeTo(FileOutputStream(destination.fileDescriptor))
        } catch (e: IOException) {
            callback.onWriteFailed(e.toString())
            return
        } finally {
            //pdfDocument.close()
        }
        val writtenPages = computeWrittenPages()
        // Signal the print framework the document is complete
        callback.onWriteFinished(writtenPages)

    }

    private fun computeWrittenPages(): Array<PageRange?> {

        val pageRanges = arrayOfNulls<PageRange>(1)

        pageRanges[0] = PageRange(0, 1)

        // TODO Auto-generated method stub
        return pageRanges
    }

}
