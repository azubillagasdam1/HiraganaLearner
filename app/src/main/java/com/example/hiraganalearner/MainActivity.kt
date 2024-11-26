package com.example.hiraganalearner

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.media.MediaPlayer
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private var clickSound: MediaPlayer? = null

    private val hiragana = arrayOf(
        "あ", "い", "う", "え", "お",
        "か", "き", "く", "け", "こ",
        "さ", "し", "す", "せ", "そ",
        "た", "ち", "つ", "て", "と",
        "な", "に", "ぬ", "ね", "の",
        "は", "ひ", "ふ", "へ", "ほ",
        "ま", "み", "む", "め", "も",
        "や", "ゆ", "よ",
        "ら", "り", "る", "れ", "ろ",
        "わ", "を", "ん",
        "が", "ぎ", "ぐ", "げ", "ご",
        "ざ", "じ", "ず", "ぜ", "ぞ",
        "だ", "ぢ", "づ", "で", "ど",
        "ば", "び", "ぶ", "べ", "ぼ",
        "ぱ", "ぴ", "ぷ", "ぺ", "ぽ",
        "きゃ", "きゅ", "きょ",
        "しゃ", "しゅ", "しょ",
        "ちゃ", "ちゅ", "ちょ",
        "にゃ", "にゅ", "にょ",
        "ひゃ", "ひゅ", "ひょ",
        "みゃ", "みゅ", "みょ",
        "りゃ", "りゅ", "りょ",
        "ぎゃ", "ぎゅ", "ぎょ",
        "じゃ", "じゅ", "じょ",
        "びゃ", "びゅ", "びょ",
        "ぴゃ", "ぴゅ", "ぴょ"
    )

    private val romaji = arrayOf(
        "a", "i", "u", "e", "o",
        "ka", "ki", "ku", "ke", "ko",
        "sa", "shi", "su", "se", "so",
        "ta", "chi", "tsu", "te", "to",
        "na", "ni", "nu", "ne", "no",
        "ha", "hi", "fu", "he", "ho",
        "ma", "mi", "mu", "me", "mo",
        "ya", "yu", "yo",
        "ra", "ri", "ru", "re", "ro",
        "wa", "wo", "n",
        "ga", "gi", "gu", "ge", "go",
        "za", "ji", "zu", "ze", "zo",
        "da", "ji", "zu", "de", "do",
        "ba", "bi", "bu", "be", "bo",
        "pa", "pi", "pu", "pe", "po",
        "kya", "kyu", "kyo",
        "sha", "shu", "sho",
        "cha", "chu", "cho",
        "nya", "nyu", "nyo",
        "hya", "hyu", "hyo",
        "mya", "myu", "myo",
        "rya", "ryu", "ryo",
        "gya", "gyu", "gyo",
        "ja", "ju", "jo",
        "bya", "byu", "byo",
        "pya", "pyu", "pyo"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) // Forzar modo claro

        clickSound = MediaPlayer.create(this, R.raw.click)

        val selectedHiragana = mutableListOf<String>()
        val selectedRomaji = mutableListOf<String>()

        // Recuperar selección previa desde SharedPreferences
        val sharedPreferences = getSharedPreferences("HiraganaLearnerPrefs", MODE_PRIVATE)
        val savedSelectedHiragana = sharedPreferences.getStringSet("selectedHiragana", emptySet()) ?: emptySet()

        val scrollView = ScrollView(this).apply {
            setBackgroundColor(Color.WHITE)
        }
        val rightAlignedLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER //
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply{}


        }
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL

        }

        val selectAllButton = Button(this).apply {
            text = "Seleccionar todo"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(16, 16, 16, 16)
            }
        }

        val buttonsPerRow = 5
        val textViews = mutableListOf<TextView>()

        fun getDefaultBackgroundColor(char: String): Int {
            return when (char) {
                "あ", "い", "う", "え", "お" -> Color.WHITE
                "か", "き", "く", "け", "こ", "が", "ぎ", "ぐ", "げ", "ご" -> Color.parseColor("#d6ffc1")
                "さ", "し", "す", "せ", "そ", "ざ", "じ", "ず", "ぜ", "ぞ" -> Color.parseColor("#f6ffc1")
                "た", "だ", "ぢ", "ち", "つ", "づ", "で", "て", "と", "ど" -> Color.parseColor("#ffecc1")
                "な", "に", "ぬ", "ね", "の" -> Color.parseColor("#ffcac1")
                "ぱ", "ば", "は", "ひ", "び", "ぴ", "ぷ", "ぶ", "ふ", "へ", "べ", "ぺ", "ぽ", "ぼ", "ほ" -> Color.parseColor("#c1ffd8")
                "ま", "み", "む", "め", "も" -> Color.parseColor("#c1faff")
                "ら", "り", "る", "れ", "ろ" -> Color.parseColor("#c1daff")
                "や", "ゆ", "よ" -> Color.parseColor("#c9c1ff")
                "りゃ", "みゃ", "ぴゃ", "びゃ", "ひゃ", "にゃ", "ちゃ", "じゃ", "しゃ", "ぎゃ", "きゃ",
                "りゅ", "みゅ", "ぴゅ", "びゅ", "ひゅ", "にゅ", "ちゅ", "じゅ", "しゅ", "ぎゅ", "きゅ",
                "りょ", "みょ", "ぴょ", "びょ", "ひょ", "にょ", "ちょ", "じょ", "しょ", "ぎょ", "きょ" -> Color.parseColor("#f7c1ff")
                "わ", "を", "ん" -> Color.parseColor("#b9b9b9")
                else -> Color.WHITE
            }
        }

        for (i in hiragana.indices step buttonsPerRow) {
            val rowLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
            }

            for (j in 0 until buttonsPerRow) {
                if (i + j < hiragana.size) {
                    val char = hiragana[i + j]
                    val transliteration = romaji[i + j]

                    val textView = TextView(this).apply {
                        text = "$char\n$transliteration"
                        gravity = Gravity.CENTER
                        textSize = 18f
                        setPadding(16, 16, 16, 16)
                        layoutParams = LinearLayout.LayoutParams(150, 150).apply {
                            setMargins(16, 16, 16, 16)
                        }

                        // Restaurar estado previo o usar color por defecto
                        background = GradientDrawable().apply {
                            setColor(if (savedSelectedHiragana.contains(char)) Color.parseColor("#74f980") else getDefaultBackgroundColor(char))
                            cornerRadius = 20f
                            setStroke(3, Color.LTGRAY)
                        }
                        elevation = 8f
                    }

                    if (savedSelectedHiragana.contains(char)) {
                        selectedHiragana.add(char)
                        selectedRomaji.add(transliteration)
                    }

                    textView.setOnClickListener {
                        playClickSound()
                        val background = textView.background as GradientDrawable
                        if (selectedHiragana.contains(char)) {
                            background.setColor(getDefaultBackgroundColor(char)) // Fondo original
                            selectedHiragana.remove(char)
                            selectedRomaji.remove(transliteration)
                        } else {
                            background.setColor(Color.parseColor("#74f980")) // Fondo verde al seleccionar
                            selectedHiragana.add(char)
                            selectedRomaji.add(transliteration)
                        }

                        // Guardar cambios en SharedPreferences
                        sharedPreferences.edit()
                            .putStringSet("selectedHiragana", selectedHiragana.toSet())
                            .apply()
                    }

                    textViews.add(textView)
                    rowLayout.addView(textView)
                }
            }

            mainLayout.addView(rowLayout)
        }

        mainLayout.addView(selectAllButton)

        val controlsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        // Contenedor para los NumberPickers y el botón flotante
        val pickerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        val lowerLimitPicker = NumberPicker(this).apply {
            minValue = 1
            maxValue = 10
            value = 1
        }

        val upperLimitPicker = NumberPicker(this).apply {
            minValue = 1
            maxValue = 10
            value = 3
        }
// Crear el CheckBox debajo de los NumberPickers
        val eliminatorioCheckbox = android.widget.CheckBox(this).apply {
            text = "Eliminación\uD83D\uDC80"
            isChecked = false // Siempre desactivado inicialmente
            isEnabled = true  // Cambiable por el usuario si es necesario
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            ).apply {
                topMargin = 16

                gravity = Gravity.CENTER
            }
        }

        val fab = FloatingActionButton(this).apply {
            setImageResource(android.R.drawable.ic_media_play)
            backgroundTintList = ColorStateList.valueOf(Color.parseColor("#ff4800"))
            setOnClickListener {
                playClickSound()
                if (selectedHiragana.isEmpty()) {
                    Toast.makeText(
                        this@MainActivity,
                        "Por favor, selecciona al menos una letra para jugar.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val intent = Intent(this@MainActivity, GameActivity::class.java)
                    intent.putExtra("Hiragana", selectedHiragana.toTypedArray())
                    intent.putExtra("Romaji", selectedRomaji.toTypedArray())
                    intent.putExtra("LowerLimit", lowerLimitPicker.value)
                    intent.putExtra("UpperLimit", upperLimitPicker.value)

                    // Pasar el estado de la checkbox
                    intent.putExtra("Eliminatorio", eliminatorioCheckbox.isChecked)
                    startActivity(intent)
                }
            }
        }
        // Comportamiento del botón de seleccionar/deseleccionar todo
        selectAllButton.setOnClickListener {
            playClickSound()
            if (selectedHiragana.size == hiragana.size) {
                // Deseleccionar todo
                textViews.forEachIndexed { index, textView ->
                    val char = hiragana[index]
                    (textView.background as GradientDrawable).setColor(getDefaultBackgroundColor(char)) // Fondo original
                }
                selectedHiragana.clear()
                selectedRomaji.clear()
                selectAllButton.text = "Seleccionar todo"
            } else {
                // Seleccionar todo
                textViews.forEach {
                    (it.background as GradientDrawable).setColor(Color.parseColor("#74f980")) // Fondo verde
                }
                selectedHiragana.clear()
                selectedHiragana.addAll(hiragana)
                selectedRomaji.clear()
                selectedRomaji.addAll(romaji)
                selectAllButton.text = "Deseleccionar todo"
            }

            // Guardar cambios en SharedPreferences
            sharedPreferences.edit()
                .putStringSet("selectedHiragana", selectedHiragana.toSet())
                .apply()
        }





        rightAlignedLayout.addView(eliminatorioCheckbox)
        controlsLayout.addView(lowerLimitPicker)
        controlsLayout.addView(fab)

        controlsLayout.addView(upperLimitPicker)
        mainLayout.addView(rightAlignedLayout)
        mainLayout.addView(controlsLayout)
        scrollView.addView(mainLayout)
        setContentView(scrollView)

    }



    private fun playClickSound() {
        clickSound?.let {
            if (it.isPlaying) {
                it.stop()
                it.prepare()
            }
            it.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Liberar recursos de MediaPlayer
        clickSound?.release()
    }

}
