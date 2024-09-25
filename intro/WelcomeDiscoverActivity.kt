package com.inpower.webguruz.intro

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.inpower.webguruz.R
import com.inpower.webguruz.model.CardItem
import com.inpower.webguruz.recyclerviewswip.CardAdapter

class WelcomeDiscoverActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var cardAdapter: CardAdapter
    private lateinit var cardItemList: MutableList<CardItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.intro_group_layout)

        recyclerView = findViewById(R.id.rv_group)
        recyclerView.layoutManager = LinearLayoutManager(this)

        cardItemList = mutableListOf(
            CardItem("Mom-Power", "The ultimate groups for mommys! Share tips and tricks on what helped you be a successful mom...", R.drawable.mompower, "+5k"),
            CardItem("Fitness", "Join the best fitness community and achieve your health goals together...", R.drawable.fitness, "+5k")
            // Add more cards as needed
        )

        cardAdapter = CardAdapter(cardItemList)
        recyclerView.adapter = cardAdapter
    }
}
