package com.example.memorina

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val faces = listOf(
        R.drawable.card_1,
        R.drawable.card_2,
        R.drawable.card_3,
        R.drawable.card_4,
        R.drawable.card_5,
        R.drawable.card_6,
        R.drawable.card_7,
        R.drawable.card_8
    )

    private val cardViews = mutableListOf<ImageView>()
    private var firstCard: ImageView? = null
    private var isBusy: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        startNewGame()
    }

    private fun startNewGame() {
        val board = findViewById<LinearLayout>(R.id.board)
        board.removeAllViews()
        cardViews.clear()
        firstCard = null
        isBusy = false

        val cards = (faces + faces).shuffled()
        val rows = Array(4) {
            LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f)
            }
        }

        cards.forEachIndexed { index, face ->
            val card = ImageView(this).apply {
                setImageResource(R.drawable.card_back)
                tag = face
                visibility = View.VISIBLE
                isClickable = true
                scaleType = ImageView.ScaleType.FIT_CENTER
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f).apply {
                    setMargins(4, 4, 4, 4)
                }
                setOnClickListener { openCard(this) }
            }
            cardViews.add(card)
            rows[index / 4].addView(card)
        }
        rows.forEach { board.addView(it) }
    }

    private fun openCard(card: ImageView) {
        if (isBusy || card == firstCard || card.visibility != View.VISIBLE) return

        card.setImageResource(card.tag as Int)

        val currentFirst = firstCard
        if (currentFirst == null) {
            firstCard = card
        } else {
            isBusy = true
            lifecycleScope.launch {
                if (currentFirst.tag == card.tag) {
                    delay(500)
                    currentFirst.visibility = View.INVISIBLE
                    currentFirst.isClickable = false
                    card.visibility = View.INVISIBLE
                    card.isClickable = false
                    firstCard = null
                    isBusy = false
                    checkWin()
                } else {
                    delay(1000)
                    currentFirst.setImageResource(R.drawable.card_back)
                    card.setImageResource(R.drawable.card_back)
                    firstCard = null
                    isBusy = false
                }
            }
        }
    }

    private fun checkWin() {
        if (cardViews.all { it.visibility == View.INVISIBLE }) {
            Toast.makeText(this, "Победа! Все карты открыты!", Toast.LENGTH_SHORT).show()
            AlertDialog.Builder(this)
                .setTitle("Победа!")
                .setMessage("Поздравляем! Вы нашли все пары карт. Сыграть еще раз?")
                .setPositiveButton("Да") { _, _ -> startNewGame() }
                .setNegativeButton("Отмена", null)
                .show()
        }
    }
}
