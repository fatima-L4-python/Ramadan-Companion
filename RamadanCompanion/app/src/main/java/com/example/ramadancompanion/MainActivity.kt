package com.example.ramadancompanion

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.button.MaterialButton
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

    // Good Deeds tracker variables
    private var totalPoints = 10
    private var streakDays = 3
    private var currentLevel = "Bronze"

    // Good deed point values
    private val pointValues = mapOf(
        "quran" to 10,
        "charity" to 15,
        "taraweeh" to 20,
        "help" to 15,
        "dua" to 5,
        "food" to 10
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI elements
        initializeViews()
        setupButtonListeners()
        updateZakatCalculation()
        updateMainUiWithGoodDeedsScore() // Initialize main UI with default values
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

        // Prayer Times button
        val prayerTimesButton: Button = findViewById(R.id.prayerTimesButton)
        prayerTimesButton.setOnClickListener {
            showPrayerTimesDialog()
        }

        // Good Deeds button
        val goodDeedsButton: Button = findViewById(R.id.goodDeedsButton)
        goodDeedsButton.setOnClickListener {
            showGoodDeedsDialog()
        }

        // Details text
        val detailsText: TextView = findViewById(R.id.detailsText)
        detailsText.setOnClickListener {
            showGoodDeedsDialog()
        }
    }

    private fun showPrayerTimesDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_prayer_times)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Set up prayer times - these would normally be calculated or fetched
        val fajrTime: TextView = dialog.findViewById(R.id.fajrTimeText)
        val sunriseTime: TextView = dialog.findViewById(R.id.sunriseTimeText)
        val dhuhrTime: TextView = dialog.findViewById(R.id.dhuhrTimeText)
        val asrTime: TextView = dialog.findViewById(R.id.asrTimeText)
        val maghribTime: TextView = dialog.findViewById(R.id.maghribTimeText)
        val ishaTime: TextView = dialog.findViewById(R.id.ishaTimeText)

        // Sample prayer times - in a real app, these would be calculated based on location
        fajrTime.text = "4:35 AM"
        sunriseTime.text = "6:03 AM"
        dhuhrTime.text = "12:17 PM"
        asrTime.text = "3:45 PM"
        maghribTime.text = "6:30 PM"
        ishaTime.text = "7:55 PM"

        // Close button
        val closeButton: Button = dialog.findViewById(R.id.closeButton)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showGoodDeedsDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_good_deeds)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Initialize score and level displays
        val totalScoreText: TextView = dialog.findViewById(R.id.totalScoreText)
        val currentLevelText: TextView = dialog.findViewById(R.id.currentLevelText)

        totalScoreText.text = "$totalPoints points"
        currentLevelText.text = "$currentLevel 🏆"

        // Set up add buttons for each good deed
        setupGoodDeedButton(dialog, R.id.addQuranButton, "quran")
        setupGoodDeedButton(dialog, R.id.addCharityButton, "charity")
        setupGoodDeedButton(dialog, R.id.addTaraweehButton, "taraweeh")
        setupGoodDeedButton(dialog, R.id.addHelpButton, "help")
        setupGoodDeedButton(dialog, R.id.addDuaButton, "dua")
        setupGoodDeedButton(dialog, R.id.addFoodButton, "food")

        // Add checkmark to "Share food for iftar" if it matches the example screenshot
        val addFoodButton: MaterialButton = dialog.findViewById(R.id.addFoodButton)
        addFoodButton.text = "✓"

        // Close button
        val closeButton: Button = dialog.findViewById(R.id.closeButton)
        closeButton.setOnClickListener {
            // Update the main UI with new score before closing
            updateMainUiWithGoodDeedsScore()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setupGoodDeedButton(dialog: Dialog, buttonId: Int, deedType: String) {
        val button: MaterialButton = dialog.findViewById(buttonId)
        button.setOnClickListener {
            // Add points for this deed
            val points = pointValues[deedType] ?: 0
            totalPoints += points

            // Update level based on new total
            updateGoodDeedsLevel()

            // Update score display in dialog
            val totalScoreText: TextView = dialog.findViewById(R.id.totalScoreText)
            val currentLevelText: TextView = dialog.findViewById(R.id.currentLevelText)

            totalScoreText.text = "$totalPoints points"
            currentLevelText.text = "$currentLevel 🏆"

            // Change button appearance to indicate completion
            button.text = "✓"
        }
    }

    private fun updateGoodDeedsLevel() {
        currentLevel = when {
            totalPoints >= 100 -> "Gold"
            totalPoints >= 50 -> "Silver"
            else -> "Bronze"
        }
    }

    private fun updateMainUiWithGoodDeedsScore() {
        // Update the main UI rank and points text
        val rankText: TextView = findViewById(R.id.rankText)
        val pointsText: TextView = findViewById(R.id.pointsText)

        rankText.text = currentLevel
        pointsText.text = "$totalPoints points • $streakDays day streak"
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