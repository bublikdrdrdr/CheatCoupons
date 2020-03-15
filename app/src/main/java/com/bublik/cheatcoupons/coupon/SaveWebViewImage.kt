package com.bublik.cheatcoupons.coupon

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import android.webkit.WebView
import java.io.File
import java.io.IOException


class SaveWebViewImage(
    private val onTaskEnd: (SaveFileResult) -> Unit
) : AsyncTask<WebView, Unit, SaveFileResult>() {

    private val maxFileIndex = 99
    private val fileName = "coupon"
    private val fileFormat = "pdf"

    override fun doInBackground(vararg params: WebView?): SaveFileResult {
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

    private fun writeToFile(file: File, container: WebView) {
        val bitmap = Bitmap.createBitmap(
            container.measuredWidth,
            container.measuredHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        val paint = Paint()
        canvas.drawBitmap(bitmap, 0f, bitmap.height.toFloat(), paint)
        container.draw(canvas)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, file.outputStream())
    }

    private fun resolveNewFile(): File {
        val outputDirectory = Environment.getExternalStorageDirectory()
            .resolve(Environment.DIRECTORY_DOCUMENTS)

        if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
            throw IOException("Documents directory does not exist: ${outputDirectory.path}")
        }

        val outputFile = outputDirectory.resolve("coupon.png")

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