package edu.utap.movieapp

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.utap.movieapp.model.Review

class ViewModelDBHelper() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collectionRoot = "allReviews"

    private fun elipsizeString(string: String) : String {
        if(string.length < 10)
            return string
        return string.substring(0..9) + "..."
    }

    fun fetchInitialReviews(reviewsList: MutableLiveData<List<Review>>,
                          callback:()->Unit) {
        dbFetchReviews(reviewsList, callback)
    }
    /////////////////////////////////////////////////////////////
    // Interact with Firestore db
    // https://firebase.google.com/docs/firestore/query-data/get-data
    //
    // If we want to listen for real time updates use this
    // .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
    // But be careful about how listener updates live data
    // and noteListener?.remove() in onCleared
    private fun dbFetchReviews(reviewsList: MutableLiveData<List<Review>>,
                             callback:()->Unit = {}) {
        db.collection(collectionRoot)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(100)
            .get()
            .addOnSuccessListener { result ->
                Log.d(javaClass.simpleName, "allReviews fetch ${result!!.documents.size}")
                // NB: This is done on a background thread
                reviewsList.postValue(result.documents.mapNotNull {
                    it.toObject(Review::class.java)
                })
                callback()
                db.collection(collectionRoot)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(100)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Log.e(javaClass.simpleName, "Error fetching reviews: $error")
                            return@addSnapshotListener
                        }

                        val updatedReviews = snapshot?.documents?.mapNotNull { document ->
                            document.toObject(Review::class.java)
                        } ?: emptyList()

                        reviewsList.postValue(updatedReviews)
                    }
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "allReviews fetch FAILED ", it)
                callback()
            }
    }

    // After we successfully modify the db, we refetch the contents to update our
    // live data.  Hence the dbFetchNotes() calls below.
    fun updateReview(
        review: Review,
        reviewsList: MutableLiveData<List<Review>>
    ) {
        val posterPath = review.poster_path
        //SSS
        db.collection(collectionRoot)
            .document(review.firestoreID)
            .set(review)
                //EEE // XXX Writing a note
            .addOnSuccessListener {
                Log.d(
                    javaClass.simpleName,
                    "Review update \"${elipsizeString(review.text)}\" len ${posterPath} id: ${review.firestoreID}"
                )
                dbFetchReviews(reviewsList)
            }
            .addOnFailureListener { e ->
                Log.d(javaClass.simpleName, "Review update FAILED \"${elipsizeString(review.text)}\"")
                Log.w(javaClass.simpleName, "Error ", e)
            }
    }

    fun createReview(
        review: Review,
        reviewsList: MutableLiveData<List<Review>>
    ) {
        // We can get ID locally
        // note.firestoreID = db.collection("allNotes").document().id

        db.collection(collectionRoot)
            .add(review)
            .addOnSuccessListener {
                Log.d(
                    javaClass.simpleName,
                    "Review create \"${elipsizeString(review.text)}\" id: ${review.firestoreID}"
                )
                dbFetchReviews(reviewsList)
            }
            .addOnFailureListener { e ->
                Log.d(javaClass.simpleName, "Review create FAILED \"${elipsizeString(review.text)}\"")
                Log.w(javaClass.simpleName, "Error ", e)
            }
    }

    fun removeReview(
        review: Review,
        reviewsList: MutableLiveData<List<Review>>
    ) {
        db.collection(collectionRoot)
            .document(review.firestoreID)
            .delete()
            .addOnSuccessListener {
                Log.d(
                    javaClass.simpleName,
                    "Review delete \"${elipsizeString(review.text)}\" id: ${review.firestoreID}"
                )
                dbFetchReviews(reviewsList)
            }
            .addOnFailureListener { e ->
                Log.d(javaClass.simpleName, "Review deleting FAILED \"${elipsizeString(review.text)}\"")
                Log.w(javaClass.simpleName, "Error adding document", e)
            }
    }
}