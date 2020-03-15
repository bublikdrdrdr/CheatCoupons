package com.bublik.cheatcoupons.coupon

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bublik.cheatcoupons.R
import com.bublik.cheatcoupons.coupon.properties.PropertiesFragment
import com.bublik.cheatcoupons.data.AssetProvider
import com.google.android.material.snackbar.Snackbar

class PdfViewerCouponFragment : AbstractCouponFragment() {

    private val assetProvider: AssetProvider = { s -> requireActivity().assets.open(s) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.pdf_viewer_fragment, container, false).also {
            setupToolbar(it)
            setupWebView(it)
            loadContent(it)
        }
    }

    private fun setupWebView(parent: View) {
        with(parent.findViewById<WebView>(R.id.couponWebView).settings) {
            useWideViewPort = true
            builtInZoomControls = true
            displayZoomControls = false
        }
    }

    private fun loadContent(parent: View) {
        val content = model.item.getContent(assetProvider) as String
        val encodedHtml =
            Base64.encodeToString(content.toByteArray(Charsets.UTF_16), Base64.NO_PADDING)
        with(parent.findViewById<WebView>(R.id.couponWebView)) {
            loadData(encodedHtml, "text/html; charset=utf-16", "base64")
            setInitialScale(1)
        }
    }

    private fun setupToolbar(parent: View) {
        parent.findViewById<Toolbar>(R.id.pdfCouponToolbar).apply {
            title = getString(R.string.pdf_viewer_title)
            setOnMenuItemClickListener(this@PdfViewerCouponFragment::onMenuClick)
        }
    }

    private fun onMenuClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.setup -> showConfigDialog()
            R.id.saveAsPdf -> askForPermissionAndSavePdf()
        }
        return true
    }

    private fun showConfigDialog() {
        val fragment = PropertiesFragment()
        fragment.show(childFragmentManager, null)
        fragment.callback = { loadContent(view!!) }
    }

    private fun askForPermissionAndSavePdf() {
        val permissionCheck = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_PHONE_STATE
        )
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.READ_PHONE_STATE
                )
            ) {
                showPermissionRequired()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    ASK_SAVE_PDF_PERMISSION
                )
            }
        } else {
            saveAsPdf()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            ASK_SAVE_PDF_PERMISSION -> {
                if (grantResults.isNotEmpty()
                    && grantResults.contains(PackageManager.PERMISSION_GRANTED)
                ) {
                    saveAsPdf()
                } else {
                    showPermissionRequired()
                }
            }
        }
    }

    private fun showPermissionRequired() {
        view?.run {
            Snackbar.make(
                this,
                R.string.save_pdf_permission_required,
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun saveAsPdf() {
        val webView = requireView().findViewById<WebView>(R.id.couponWebView)

        if (checkWebView(webView)) {
            prepareWebView(webView)
            SaveWebViewImage { it ->
                Toast.makeText(context, getMessage(it), Toast.LENGTH_LONG).show()
            }
                .execute(webView)
        } else {
            showWebViewInfo()
        }
    }

    private fun checkWebView(webView: WebView): Boolean {
        return webView.measuredHeight.toFloat() / webView.measuredWidth.toFloat() > MIN_WEB_VIEW_RATIO
    }

    private fun prepareWebView(webView: WebView) {
        webView.zoomBy(MIN_ZOOM)
    }

    private fun showWebViewInfo() {
        view?.let { view ->
            Snackbar.make(view, R.string.web_view_info, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.why) {
                    Snackbar.make(view, R.string.web_view_info_explain, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.so) {
                            Snackbar.make(
                                view,
                                R.string.web_view_into_explain_2,
                                Snackbar.LENGTH_INDEFINITE
                            ).setAction(R.string.ah_ok) {
                                Snackbar.make(
                                    view,
                                    R.string.web_view_into_explain_3,
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }.show()
                        }.show()
                }.show()
        }
    }

    private fun getMessage(taskResult: SaveFileResult): String {
        return when (taskResult.exceptionCode) {
            null -> getString(R.string.save_pdf_successful, taskResult.path)
            SaveFileResult.ExceptionCode.FILES_EXIST -> getString(
                R.string.save_file_exists,
                taskResult.exceptionData
            )
            SaveFileResult.ExceptionCode.IO -> getString(
                R.string.save_file_io,
                taskResult.exceptionData
            )
            SaveFileResult.ExceptionCode.OTHER -> getString(
                R.string.save_file_other,
                taskResult.exceptionData
            )
        }
    }

    companion object {
        const val ASK_SAVE_PDF_PERMISSION = 1
        const val MIN_WEB_VIEW_RATIO = 1.5f
        const val MIN_ZOOM = 0.02f
    }
}

