package com.ekin.app

import android.graphics.Bitmap
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID

object ImageStorage {
    private val storage = FirebaseStorage.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun uploadPlantImage(bitmap: Bitmap): Result<String> {
        return try {
            val user = auth.currentUser
            if (user == null) {
                return Result.failure(Exception("Kullanıcı giriş yapmamış"))
            }

            // Bitmap'i byte array'e çevir
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
            val data = baos.toByteArray()

            // Firebase Storage'a yükle
            val imageId = UUID.randomUUID().toString()
            val imageRef = storage.reference
                .child("plant_images")
                .child(user.uid)
                .child("$imageId.jpg")

            val uploadTask = imageRef.putBytes(data).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await().toString()

            // Firestore'da metadata kaydet
            val imageData = hashMapOf(
                "userId" to user.uid,
                "imageUrl" to downloadUrl,
                "uploadedAt" to com.google.firebase.Timestamp.now(),
                "imageId" to imageId
            )

            firestore.collection("plant_images")
                .document(imageId)
                .set(imageData)
                .await()

            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserPlantImages(): List<PlantImage> {
        return try {
            val user = auth.currentUser
            if (user == null) {
                return emptyList()
            }

            val snapshot = firestore.collection("plant_images")
                .whereEqualTo("userId", user.uid)
                .orderBy("uploadedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.map { doc ->
                PlantImage(
                    id = doc.id,
                    imageUrl = doc.getString("imageUrl") ?: "",
                    uploadedAt = doc.getTimestamp("uploadedAt")?.toDate() ?: java.util.Date()
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}

data class PlantImage(
    val id: String,
    val imageUrl: String,
    val uploadedAt: java.util.Date
)

