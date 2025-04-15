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
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    // UI elements
    private lateinit var calendarCardView: CardView
    private lateinit var wealthEditText: EditText
    private lateinit var zakatAmountTextView: TextView
    private lateinit var ayahTextView: TextView

    // Calendar and dates
    private val ramadanAyahs = mutableMapOf<Int, String>()
    private var selectedDate = 1

    // Calculator related variables
    private var currentInput = "0"
    private val zakatRate = 0.025 // 2.5%
    private val nisabThreshold = 800.0 // USD

    // Good Deeds tracker variables
    private var totalPoints = 65
    private var streakDays = 3
    private var currentLevel = "Gold"

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

        // Initialize Ramadan ayahs
        initializeRamadanAyahs()

        // Initialize UI elements
        initializeViews()
        setupButtonListeners()
        updateZakatCalculation()
        updateMainUiWithGoodDeedsScore() // Initialize main UI with default values

        // Set initial ayah
        updateAyahForDay(1)
    }

    private fun initializeRamadanAyahs() {
        ramadanAyahs[1] = "And when My servants ask you about Me, then surely I am near. I respond to the call of the caller when he calls upon Me. - Al-Baqarah 2:186"
        ramadanAyahs[2] = "Ramadan is the month in which the Quran was revealed as guidance for mankind with clear proofs of guidance and the criterion. - Al-Baqarah 2:185"
        ramadanAyahs[3] = "O you who believe! Fasting is prescribed for you as it was prescribed for those before you, that you may become righteous. - Al-Baqarah 2:183"
        ramadanAyahs[4] = "Indeed, We sent it down during a blessed night. Indeed, We were to warn [mankind]. - Ad-Dukhan 44:3"
        ramadanAyahs[5] = "Whoever does an atom's weight of good will see it, and whoever does an atom's weight of evil will see it. - Az-Zalzalah 99:7-8"
        ramadanAyahs[6] = "The Night of Decree is better than a thousand months. - Al-Qadr 97:3"
        ramadanAyahs[7] = "Who is it that would loan Allah a goodly loan so He may multiply it for him many times over? - Al-Baqarah 2:245"
        ramadanAyahs[8] = "And be patient, for indeed, Allah does not allow to be lost the reward of those who do good. - Hud 11:115"
        ramadanAyahs[9] = "Indeed, Allah orders justice and good conduct and giving to relatives and forbids immorality and bad conduct and oppression. - An-Nahl 16:90"
        ramadanAyahs[10] = "And your Lord says, 'Call upon Me; I will respond to you.' - Ghafir 40:60"
        ramadanAyahs[11] = "Those who spend their wealth in charity day and night, secretly and openly, they will have their reward with their Lord. - Al-Baqarah 2:274"
        ramadanAyahs[12] = "So remember Me; I will remember you. - Al-Baqarah 2:152"
        ramadanAyahs[13] = "And seek help through patience and prayer. Indeed, it is difficult except for the humbly submissive. - Al-Baqarah 2:45"
        ramadanAyahs[14] = "Indeed, Allah does not wrong the people at all, but it is the people who are wronging themselves. - Yunus 10:44"
        ramadanAyahs[15] = "Indeed, Allah is with those who are patient. - Al-Baqarah 2:153"
        ramadanAyahs[16] = "And give good tidings to those who believe and do righteous deeds that they will have gardens beneath which rivers flow. - Al-Baqarah 2:25"
        ramadanAyahs[17] = "Indeed, Allah loves those who are constantly repentant and loves those who purify themselves. - Al-Baqarah 2:222"
        ramadanAyahs[18] = "For those who do good is the best reward and extra. - Yunus 10:26"
        ramadanAyahs[19] = "And whoever puts all his trust in Allah, then He will suffice him. - At-Talaq 65:3"
        ramadanAyahs[20] = "And whoever fears Allah - He will make for him a way out. - At-Talaq 65:2"
        ramadanAyahs[21] = "And be steadfast in prayer and regular in charity. - Al-Baqarah 2:43"
        ramadanAyahs[22] = "Indeed, Allah does not alter the condition of a people until they alter what is in themselves. - Ar-Ra'd 13:11"
        ramadanAyahs[23] = "Indeed, prayer prohibits immorality and wrongdoing. - Al-Ankabut 29:45"
        ramadanAyahs[24] = "And whoever is grateful, he is only grateful for the benefit of his own self. - Luqman 31:12"
        ramadanAyahs[25] = "And We have certainly made the Quran easy for remembrance, so is there any who will remember? - Al-Qamar 54:17"
        ramadanAyahs[26] = "Allah intends for you ease and does not intend for you hardship. - Al-Baqarah 2:185"
        ramadanAyahs[27] = "Indeed, with hardship comes ease. Indeed, with hardship comes ease. - Ash-Sharh 94:5-6"
        ramadanAyahs[28] = "And when you have decided, then rely upon Allah. Indeed, Allah loves those who rely upon Him. - Al-Imran 3:159"
        ramadanAyahs[29] = "And those who strive for Us - We will surely guide them to Our ways. - Al-Ankabut 29:69"
        ramadanAyahs[30] = "Indeed, Allah is with the patient. - Al-Anfal 8:46"
    }

    private fun initializeViews() {
        calendarCardView = findViewById(R.id.calendarCardView)
        wealthEditText = findViewById(R.id.wealthEditText)
        zakatAmountTextView = findViewById(R.id.zakatAmountTextView)
        ayahTextView = findViewById(R.id.ayahTextView)

        // Set initial value for wealth input
        wealthEditText.setText(currentInput)
    }

    private fun setupButtonListeners() {
        // Calendar toggle
        val calendarButton: Button = findViewById(R.id.calendarButton)
        calendarButton.setOnClickListener {
            showCalendarDialog()
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

    private fun showCalendarDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_calendar)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Set up calendar in dialog
        val dialogTitle: TextView = dialog.findViewById(R.id.dialogTitle)
        dialogTitle.text = "Ramadan Fasting Calendar"

        // Close button
        val closeButton: Button = dialog.findViewById(R.id.closeButton)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        // Setup day buttons (1-30)
        for (day in 1..30) {
            val buttonId = resources.getIdentifier("day${day}Button", "id", packageName)
            if (buttonId != 0) { // Check if the resource exists
                val dayButton: Button = dialog.findViewById(buttonId)
                dayButton.text = day.toString()
                dayButton.setOnClickListener {
                    selectedDate = day
                    updateAyahForDay(day)
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }

    private fun updateAyahForDay(day: Int) {
        // Update the ayah text view with the ayah for the selected day
        val ayah = ramadanAyahs[day] ?: "And when My servants ask you about Me, then surely I am near. I respond to the call of the caller when he calls upon Me. - Al-Baqarah 2:186"
        ayahTextView.text = ayah
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
}