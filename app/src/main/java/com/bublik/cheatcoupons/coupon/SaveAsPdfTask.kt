package com.bublik.cheatcoupons.coupon

import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import com.itextpdf.text.Document
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.tool.xml.XMLWorkerHelper
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.StringReader


class SaveAsPdfTask(
    private val onTaskEnd: (SaveFileResult) -> Unit
) : AsyncTask<String, Unit, SaveFileResult>() {

    private val maxFileIndex = 99
    private val fileName = "coupon"
    private val fileFormat = "pdf"

    override fun doInBackground(vararg params: String?): SaveFileResult {
        try {
            val file = resolveNewFile()
            writeToFile(file, params[0]!!)
            return SaveFileResult(path = file.path)
        } catch (e: CannotFindUniqueName) {
            return SaveFileResult(
                exceptionCode = SaveFileResult.ExceptionCode.FILES_EXIST,
                exceptionData = maxFileIndex.toString()
            )
        } catch (e: IOException) {
            return SaveFileResult(
                exceptionCode = SaveFileResult.ExceptionCode.IO,
                exceptionData = e.message
            )
        } catch (e: Exception) {
            Log.e("AsyncTaskError", "Can't save pdf", e)
            return SaveFileResult(
                exceptionCode = SaveFileResult.ExceptionCode.OTHER,
                exceptionData = e.message
            )
        }
    }

    private fun writeToFile(file: File, content: String) {
        val document = Document()
        val writer: PdfWriter = PdfWriter.getInstance(
            document,
            FileOutputStream(file)
        )
        document.open()
        XMLWorkerHelper.getInstance().parseXHtml(
            writer, document, StringReader(content)
        )
        document.close()
    }

    private fun resolveNewFile(): File {
        val outputDirectory = Environment.getExternalStorageDirectory()
            .resolve(Environment.DIRECTORY_DOCUMENTS)

        if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
            throw IOException("Documents directory does not exist: ${outputDirectory.path}")
        }

        val outputFile = outputDirectory.resolve("coupon.pdf")

        return if (outputFile.exists()) {
            findUniqueName(outputDirectory)
        } else {
            outputFile
        }
    }

    private fun findUniqueName(directory: File): File {
        for (i in 1..maxFileIndex) {
            val outputFile = directory.resolve(formatFileName(i))
            if (!outputFile.exists()) {
                return outputFile
            }
        }
        throw CannotFindUniqueName()
    }

    private fun formatFileName(index: Int? = null): String {
        val formattedIndex = index?.let { "($it)" } ?: ""
        return "$fileName$formattedIndex.$fileFormat"
    }

    override fun onPostExecute(result: SaveFileResult?) {
        onTaskEnd(result!!)
    }

    private class CannotFindUniqueName : RuntimeException()
}