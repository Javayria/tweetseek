package com.example.tweetseek.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.tweetseek.R
import com.example.tweetseek.databinding.IdentificationHistoryBinding
import com.example.tweetseek.model.BirdReportData
import com.example.tweetseek.adapter.HistoryAdapter
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class IdentificationHistoryActivity : AppCompatActivity() {

    private lateinit var binding: IdentificationHistoryBinding
    private lateinit var adapter: HistoryAdapter
    private val reports = mutableListOf<BirdReportData>()

    private var currentPageStart: DocumentSnapshot? = null
    private val pageStack = ArrayDeque<DocumentSnapshot?>()
    private val pageSize = 6
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = IdentificationHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupPaginationButtons()
        loadPage(next = true)
    }

    // Setup recycler view with grid layout and adapter
    private fun setupRecyclerView() {
        adapter = HistoryAdapter(reports) { report ->
            val intent = Intent(this, ResultActivity::class.java).apply {
                putExtra("bird_name", report.birdName)
                putExtra("bird_image", report.imageUrl)
                putExtra("bird_expert", report.expert)
            }
            startActivity(intent)
        }
        binding.historyRecycler.layoutManager = GridLayoutManager(this, 2)
        binding.historyRecycler.adapter = adapter

    }

    // Setup next/prev buttons, back button
    private fun setupPaginationButtons() {
        binding.nextButton.setOnClickListener { loadPage(next = true) }
        binding.prevButton.setOnClickListener { loadPage(next = false) }

        binding.backButton.setOnClickListener { onBackPressedDispatcher.onBackPressed()}
    }

    // Load a page of reports from Firestore based on pagination state
    private fun loadPage(next: Boolean) {
        lifecycleScope.launch {
            val uid = Firebase.auth.currentUser?.uid ?: return@launch

            // Base query ordered by bird name
            val baseQuery = db.collection("Users")
                .document(uid)
                .collection("reports")
                .orderBy("birdName")
                .limit(pageSize.toLong())

            // Determine if we're paginating forward or back
            val query = if (next && currentPageStart != null) {
                baseQuery.startAfter(currentPageStart!!)
            } else {
                baseQuery
            }

            // Fetch and handle results
            val docs = query.get().await().documents
            if (docs.isNotEmpty()) {
                updatePaginationState(next, docs)
                updateReportList(docs)
                updatePaginationButtons(docs.size)
            }
        }
    }

    // Save or restore pagination state
    private fun updatePaginationState(next: Boolean, docs: List<DocumentSnapshot>) {
        if (next) {
            pageStack.addLast(currentPageStart)
            currentPageStart = docs.last()
        } else if (pageStack.isNotEmpty()) {
            currentPageStart = pageStack.removeLast()
        }
    }

    // Map Firestore documents into BirdReport objects and update the adapter
    private suspend fun updateReportList(docs: List<DocumentSnapshot>) {
        val result = withContext(Dispatchers.Default) {
            docs.mapNotNull {
                val name = it.getString("birdName")
                val imageUrl = it.getString("imagePath")
                val expert = it.getString("expert") ?: "Unknown"
                if (name != null && imageUrl != null)
                    BirdReportData(imageUrl, name, expert)
                else null
            }
        }

        reports.clear()
        reports.addAll(result)
        adapter.notifyDataSetChanged()
    }

    // Enable or disable next/prev buttons based on current state
    private fun updatePaginationButtons(currentSize: Int) {
        binding.prevButton.isEnabled = pageStack.isNotEmpty()
        binding.nextButton.isEnabled = currentSize == pageSize
    }
}
