package com.example.memorina

import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        val board = findViewById<LinearLayout>(R.id.board)
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
                scaleType = ImageView.ScaleType.FIT_CENTER
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f).apply {
                    setMargins(4, 4, 4, 4)
                }
                setOnClickListener { openCard(this) }
            }
            rows[index / 4].addView(card)
        }
        rows.forEach { board.addView(it) }
    }

    private fun openCard(card: ImageView) {
        if (!card.isClickable) return
        card.isClickable = false
        card.setImageResource(card.tag as Int)
        lifecycleScope.launch {
            delay(1000)
            card.setImageResource(R.drawable.card_back)
            card.isClickable = true
        }
    }
}
