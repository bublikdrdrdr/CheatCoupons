package com.bublik.cheatcoupons.coupon.properties

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.text.InputType
import android.text.format.DateFormat
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.bublik.cheatcoupons.R
import com.bublik.cheatcoupons.coupon.CouponViewModel
import com.bublik.cheatcoupons.data.CouponProperties
import java.util.*

class PropertiesFragment : DialogFragment() {

    private lateinit var model: CouponViewModel
    private lateinit var dateFormat: java.text.DateFormat
    var callback: PropertiesFragmentCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProvider(requireActivity())[CouponViewModel::class.java]
        dateFormat = DateFormat.getDateFormat(requireContext())
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val myView =
            activity?.layoutInflater?.inflate(R.layout.coupon_parameters_fragment, null, false)
                ?.also { v ->
                    for (property in this.model.item.properties.propertyTypes) {
                        val container = v.findViewById<ViewGroup>(R.id.propertiesContainer)
                        val fieldParams = PropertyFieldParams(
                            container,
                            property.key.first,
                            property.key.second
                        )

                        when (property.value) {
                            CouponProperties.PropertyType.STRING -> addStringPropertyField(
                                fieldParams,
                                this@PropertiesFragment.model.item.properties[property.key.first]
                            )

                            CouponProperties.PropertyType.DATE -> addDatePropertyField(
                                fieldParams,
                                this@PropertiesFragment.model.item.properties[property.key.first]
                            )

                            else -> throw UnsupportedOperationException("Unsupported, yet")
                        }
                    }
                    v.findViewById<LinearLayout>(R.id.propertiesContainer)?.invalidate()
                    builder.setView(v)
                }
        builder.setPositiveButton("Apply") { _, _ ->
            this.model.item.properties.propertyTypes.entries.forEach { propertyType ->
                myView?.findViewWithTag<View>(propertyType.key.first)?.let { fieldView ->
                    setPropertyFromField(propertyType.key.first, propertyType.value, fieldView)
                }
            }
            callback?.invoke(Unit)
        }
        return builder.create()
    }

    private fun setPropertyFromField(
        code: String,
        type: CouponProperties.PropertyType,
        view: View
    ) {
        with(model.item.properties) {
            when (type) {
                CouponProperties.PropertyType.STRING -> this[code] =
                    (view as EditText).text.toString()

                CouponProperties.PropertyType.DATE -> this[code] =
                    dateFormat.parse((view as EditText).text.toString())

                else -> throw UnsupportedOperationException("Unsupported, yet")
            }
        }
    }

    override fun onDetach() {
        callback = null
        super.onDetach()
    }

    private fun addStringPropertyField(
        params: PropertyFieldParams,
        value: String
    ) {
        activity?.layoutInflater!!.inflate(R.layout.properties_string, null, false).apply {
            setLabelForField(this, params.labelRes)
            with(getFieldInput<EditText>(this)) {
                tag = params.code
                setText(value)
            }
            params.container.addView(this)
        }
    }

    private fun setLabelForField(view: View, labelRes: Int) {
        view.findViewById<TextView>(R.id.label).text = getString(labelRes)
    }

    private fun <T : View> getFieldInput(parent: View): T = parent.findViewById(R.id.input)

    private fun addDatePropertyField(params: PropertyFieldParams, value: Date) {
        activity?.layoutInflater!!.inflate(R.layout.properties_date, params.container, false).apply {
            setLabelForField(this, params.labelRes)
            with(getFieldInput<EditText>(this)) {
                tag = params.code
                setText(dateFormat.format(value))
                setOnClickListener {
                    showDatePickerDialog(it as EditText)
                }
            }
            params.container.addView(this)
        }
    }

    private fun showDatePickerDialog(field: EditText) {
        val calendar = Calendar.getInstance()
        calendar.time = dateFormat.parse(field.text.toString())!!
        with(DatePickerDialog(requireContext())) {
            updateDate(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            setOnDateSetListener { _, y, m, d ->
                with(Calendar.getInstance()) {
                    set(y, m, d)
                    field.setText(dateFormat.format(this.time))
                }
            }
            show()
        }
    }

    private data class PropertyFieldParams(
        val container: ViewGroup,
        val code: String,
        val labelRes: Int
    )
}

typealias PropertiesFragmentCallback = (Unit) -> Unit