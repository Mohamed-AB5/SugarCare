package com.example.sugercare.viewModels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.sugercare.app.SugarReading
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SugarViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val currentUserId: String
        get() = auth.currentUser?.uid ?: "test_user_123"

    var readingsList = mutableStateOf<List<SugarReading>>(emptyList())
        private set

    init {
        getReadings()
    }

    fun addReading(level: Int, note: String) {
        val reading = SugarReading(userId = currentUserId, glucoseLevel = level, note = note)
        db.collection("sugar_readings").document(reading.id).set(reading)
            .addOnFailureListener { exception ->
                Log.d("FIRESTORE", "Firestore Error: ${exception.message}")
            }
    }

    fun getReadings() {
        db.collection("sugar_readings")
            .whereEqualTo("userId", currentUserId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FIRESTORE", "Listen failed: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    readingsList.value = snapshot.toObjects(SugarReading::class.java)
                        .sortedBy { it.timestamp }
                }
            }
    }

    fun deleteReading(readingId: String) {
        db.collection("sugar_readings").document(readingId).delete()
    }
}