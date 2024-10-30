package com.example.currencycalculator

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var inputAmountEditText: EditText
    private lateinit var outputAmountEditText: EditText
    private lateinit var inputCurrencySpinner: Spinner
    private lateinit var outputCurrencySpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Currency Converter"
        }

        inputAmountEditText = findViewById(R.id.sourceAmountEditText)
        outputAmountEditText = findViewById(R.id.targetAmountEditText)
        inputCurrencySpinner = findViewById(R.id.sourceCurrencySpinner)
        outputCurrencySpinner = findViewById(R.id.targetCurrencySpinner)

        val currencies = arrayOf("USD", "EUR", "VND", "JPY", "CNY")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        inputCurrencySpinner.adapter = adapter
        outputCurrencySpinner.adapter = adapter

        inputAmountEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (inputAmountEditText.isFocused) {
                    performConversion(isInputToOutput = true)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        outputAmountEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (outputAmountEditText.isFocused) {
                    performConversion(isInputToOutput = false)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        inputCurrencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View, position: Int, id: Long) {
                performConversion(isInputToOutput = false)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        outputCurrencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View, position: Int, id: Long) {
                performConversion(isInputToOutput = true)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun performConversion(isInputToOutput: Boolean) {
        val inputAmountText = if (isInputToOutput) inputAmountEditText.text.toString() else outputAmountEditText.text.toString()

        if (inputAmountText.isNotEmpty()) {
            val inputAmount = inputAmountText.toDoubleOrNull() ?: 0.0
            val fromCurrency = if (isInputToOutput) inputCurrencySpinner.selectedItem.toString() else outputCurrencySpinner.selectedItem.toString()
            val toCurrency = if (isInputToOutput) outputCurrencySpinner.selectedItem.toString() else inputCurrencySpinner.selectedItem.toString()

            val conversionRate = fetchConversionRate(fromCurrency, toCurrency)
            val convertedAmount = inputAmount * conversionRate

            if (isInputToOutput) {
                outputAmountEditText.setText(String.format(Locale.US, "%.2f", convertedAmount))
            } else {
                inputAmountEditText.setText(String.format(Locale.US, "%.2f", convertedAmount))
            }
        } else {
            if (isInputToOutput) {
                outputAmountEditText.setText("")
            } else {
                inputAmountEditText.setText("")
            }
        }
    }

    private fun fetchConversionRate(fromCurrency: String, toCurrency: String): Double {
        return when (fromCurrency to toCurrency) {
            "VND" to "VND" -> 1.0
            "VND" to "USD" -> 0.00003946
            "VND" to "EUR" -> 0.00003645
            "VND" to "JPY" -> 0.006035
            "VND" to "CNY" -> 0.0002825

            "USD" to "USD" -> 1.0
            "USD" to "VND" -> 25345.0
            "USD" to "EUR" -> 0.9239
            "USD" to "JPY" -> 149.89
            "USD" to "CNY" -> 3536.69

            "EUR" to "EUR" -> 1.0
            "EUR" to "VND" -> 27432.6226
            "EUR" to "USD" -> 1.0824
            "EUR" to "JPY" -> 162.26
            "EUR" to "CNY" -> 3726.39

            "JPY" to "JPY" -> 1.0
            "JPY" to "VND" -> 165.7077
            "JPY" to "USD" -> 0.006683
            "JPY" to "EUR" -> 0.006157
            "JPY" to "CNY" -> 0.0060277

            "CNY" to "CNY" -> 1.0
            "CNY" to "VND" -> 3551.16
            "CNY" to "USD" -> 0.0002824
            "CNY" to "EUR" -> 0.0002679
            "CNY" to "JPY" -> 165.53

            else -> 1.0
        }
    }
}
