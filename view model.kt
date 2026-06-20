package com.example.sugercare.app

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class SugarViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val currentUserId: String
        get() = auth.currentUser?.uid ?: "test_user_123"

    var readingsList = mutableStateOf<List<SugarReading>>(emptyList())
        private set

    init { getReadings() }

    fun addReading(level: Int, note: String) {
        val reading = SugarReading(userId = currentUserId, glucoseLevel = level, note = note)
        db.collection("sugar_readings").document(reading.id).set(reading)
    }

    fun getReadings() {
        db.collection("sugar_readings")
            .whereEqualTo("userId", currentUserId)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    readingsList.value = snapshot.toObjects(SugarReading::class.java)
                }
            }
    }

    fun deleteReading(readingId: String) {
        db.collection("sugar_readings").document(readingId).delete()
    }
}