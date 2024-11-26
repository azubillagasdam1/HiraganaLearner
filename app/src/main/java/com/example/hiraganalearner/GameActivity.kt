package com.example.hiraganalearner
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import kotlin.random.Random

class GameActivity : AppCompatActivity() {

    private lateinit var wordTextView: TextView
    private lateinit var inputEditText: EditText
    private lateinit var nextButton: Button
    private lateinit var scoreTextView: TextView
    private lateinit var rootLayout: LinearLayout

    private lateinit var hiraganaList: Array<String>
    private lateinit var romajiList: Array<String>
    private var lowerLimit: Int = 1
    private var upperLimit: Int = 1

    private var currentHiraganaWord = ""
    private var currentRomajiWord = ""
    private var score = 0

    private var clickSound: MediaPlayer? = null
    private var correctSound: MediaPlayer? = null
    private var incorrectSound: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) // Forzar modo claro

        setContentView(R.layout.activity_game)

        // Referencias a vistas
        rootLayout = findViewById(R.id.rootLayout)
        wordTextView = findViewById(R.id.wordTextView)
        inputEditText = findViewById(R.id.inputEditText)
        nextButton = findViewById(R.id.nextButton)
        scoreTextView = findViewById(R.id.scoreTextView)

        // Inicializar sonidos
        clickSound = MediaPlayer.create(this, R.raw.click)
        correctSound = MediaPlayer.create(this, R.raw.correcto)
        incorrectSound = MediaPlayer.create(this, R.raw.incorrecto)

        // Obtener parámetros
        hiraganaList = intent.getStringArrayExtra("Hiragana") ?: emptyArray()
        romajiList = intent.getStringArrayExtra("Romaji") ?: emptyArray()
        lowerLimit = intent.getIntExtra("LowerLimit", 1)
        upperLimit = intent.getIntExtra("UpperLimit", 1)


        wordTextView.setOnLongClickListener {
            Toast.makeText(this, "Romaji: $currentRomajiWord", Toast.LENGTH_SHORT).show()
            true // Indicar que el evento se ha manejado
        }

        // Inicializar puntuación
        updateScore(0)

        // Generar la primera palabra
        generateNewWord()

        // Manejo del botón "Siguiente"
        nextButton.text = "→" // Cambiar el texto a flecha
        nextButton.setOnClickListener {
            playSound(clickSound)
            checkAnswer()
        }

        // Detectar el evento "Enter" en el teclado virtual
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO) {
                playSound(clickSound)
                checkAnswer()
                true
            } else {
                false
            }
        }
    }

    private fun generateNewWord() {
        // Generar una palabra aleatoria en hiragana y su transliteración en romaji
        val wordLength = Random.nextInt(lowerLimit, upperLimit + 1)
        val hiraganaBuilder = StringBuilder()
        val romajiBuilder = StringBuilder()

        repeat(wordLength) {
            val randomIndex = Random.nextInt(hiraganaList.size)
            hiraganaBuilder.append(hiraganaList[randomIndex])
            romajiBuilder.append(romajiList[randomIndex])
        }

        currentHiraganaWord = hiraganaBuilder.toString()
        currentRomajiWord = romajiBuilder.toString()

        // Mostrar la palabra en hiragana
        wordTextView.text = currentHiraganaWord
    }

    private fun checkAnswer() {
        val userInput = inputEditText.text.toString().trim()

        if (userInput.equals(currentRomajiWord, ignoreCase = true)) {
            // Respuesta correcta: incrementar puntuación y cambiar fondo a verde
            val wordLength = currentHiraganaWord.length
            updateScore(wordLength)
            playSound(correctSound)
            animateBackgroundTransition(Color.parseColor("#00ff00")) // Verde
        } else {
            // Respuesta incorrecta: cambiar fondo a rojo y mostrar romaji en un Toast
            playSound(incorrectSound)
            animateBackgroundTransition(Color.parseColor("#ff0000")) // Rojo

            Toast.makeText(this, "La palabra era: $currentRomajiWord", Toast.LENGTH_SHORT).show()

            // Verificar si es "Eliminatorio"
            val isEliminatorio = intent.getBooleanExtra("Eliminatorio", false)
            if (isEliminatorio) {
                // Cambiar a MainActivity después de 0.5 segundos
                Handler(Looper.getMainLooper()).postDelayed({

                    val mainIntent = Intent(this, MainActivity::class.java)
                    startActivity(mainIntent)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish() // Cierra esta actividad
                }, 500) // Retraso de 500 ms
            }
        }
    }


    private fun animateBackgroundTransition(startColor: Int) {
        val duration = 500L // Duración de la transición en milisegundos
        val steps = 50 // Número de pasos en la transición
        val delay = duration / steps // Tiempo entre cada paso

        val startRed = Color.red(startColor)
        val startGreen = Color.green(startColor)
        val startBlue = Color.blue(startColor)

        val endRed = Color.red(Color.WHITE)
        val endGreen = Color.green(Color.WHITE)
        val endBlue = Color.blue(Color.WHITE)

        val handler = Handler(Looper.getMainLooper())
        for (i in 0..steps) {
            handler.postDelayed({
                val fraction = i / steps.toFloat()
                val newRed = (startRed + fraction * (endRed - startRed)).toInt()
                val newGreen = (startGreen + fraction * (endGreen - startGreen)).toInt()
                val newBlue = (startBlue + fraction * (endBlue - startBlue)).toInt()
                rootLayout.setBackgroundColor(Color.rgb(newRed, newGreen, newBlue))

                // Al final del bucle, generar nueva palabra y limpiar entrada
                if (i == steps) {
                    generateNewWord()
                    inputEditText.text.clear()
                }
            }, i * delay)
        }
    }

    private fun updateScore(points: Int) {
        score += points
        scoreTextView.text = "Puntuación: $score"
    }

    private fun playSound(mediaPlayer: MediaPlayer?) {
        mediaPlayer?.let {
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
        correctSound?.release()
        incorrectSound?.release()
    }
}
