package com.example.ramadancompanion

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    // UI elements
    private lateinit var calendarCardView: CardView
    private lateinit var wealthEditText: EditText
    private lateinit var zakatAmountTextView: TextView

    // Calculator related variables
    private var currentInput = "0"
    private val zakatRate = 0.025 // 2.5%
    private val nisabThreshold = 800.0 // USD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI elements
        initializeViews()
        setupButtonListeners()
        updateZakatCalculation()
    }

    private fun initializeViews() {
        calendarCardView = findViewById(R.id.calendarCardView)
        wealthEditText = findViewById(R.id.wealthEditText)
        zakatAmountTextView = findViewById(R.id.zakatAmountTextView)

        // Set initial value for wealth input
        wealthEditText.setText(currentInput)
    }

    private fun setupButtonListeners() {
        // Calendar toggle
        val calendarButton: Button = findViewById(R.id.calendarButton)
        calendarButton.setOnClickListener {
            toggleCalendarView()
        }

        // Numeric buttons
        setupNumericButton(R.id.button0, "0")
        setupNumericButton(R.id.button1, "1")
        setupNumericButton(R.id.button2, "2")
        setupNumericButton(R.id.button3, "3")
        setupNumericButton(R.id.button4, "4")
        setupNumericButton(R.id.button5, "5")
        setupNumericButton(R.id.button6, "6")
        setupNumericButton(R.id.button7, "7")
        setupNumericButton(R.id.button8, "8")
        setupNumericButton(R.id.button9, "9")

        // Decimal button
        val decimalButton: Button = findViewById(R.id.buttonDecimal)
        decimalButton.setOnClickListener {
            if (!currentInput.contains(".")) {
                appendDigit(".")
            }
        }

        // Clear button
        val clearButton: Button = findViewById(R.id.buttonClear)
        clearButton.setOnClickListener {
            currentInput = "0"
            updateDisplay()
        }

        // Delete button
        val deleteButton: Button = findViewById(R.id.buttonDelete)
        deleteButton.setOnClickListener {
            if (currentInput.length > 1) {
                currentInput = currentInput.substring(0, currentInput.length - 1)
            } else {
                currentInput = "0"
            }
            updateDisplay()
        }

        // Other navigation buttons
        val prayerTimesButton: Button = findViewById(R.id.prayerTimesButton)
        prayerTimesButton.setOnClickListener {
            // TODO: Navigate to Prayer Times screen
        }

        val goodDeedsButton: Button = findViewById(R.id.goodDeedsButton)
        goodDeedsButton.setOnClickListener {
            // TODO: Navigate to Good Deeds screen
        }

        // Details text
        val detailsText: TextView = findViewById(R.id.detailsText)
        detailsText.setOnClickListener {
            // TODO: Show Good Deeds details dialog
        }
    }

    private fun setupNumericButton(buttonId: Int, digit: String) {
        val button: Button = findViewById(buttonId)
        button.setOnClickListener {
            appendDigit(digit)
        }
    }

    private fun appendDigit(digit: String) {
        if (currentInput == "0" && digit != ".") {
            currentInput = digit
        } else {
            currentInput += digit
        }
        updateDisplay()
    }

    private fun updateDisplay() {
        wealthEditText.setText(currentInput)
        updateZakatCalculation()
    }

    private fun updateZakatCalculation() {
        try {
            val wealth = currentInput.toDouble()
            val zakatAmount = if (wealth >= nisabThreshold) {
                wealth * zakatRate
            } else {
                0.0
            }

            // Format the zakat amount with currency
            val formatter = NumberFormat.getCurrencyInstance(Locale.US)
            zakatAmountTextView.text = formatter.format(zakatAmount)

        } catch (e: NumberFormatException) {
            zakatAmountTextView.text = "$0.00"
        }
    }

    fun toggleCalendarView(view: View? = null) {
        if (calendarCardView.visibility == View.VISIBLE) {
            calendarCardView.visibility = View.GONE
        } else {
            calendarCardView.visibility = View.VISIBLE
        }
    }
}