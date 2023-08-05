package com.garudpuran.postermakerpro.data.repositories

import android.net.Uri
import com.garudpuran.postermakerpro.data.interfaces.HomeRepo
import com.garudpuran.postermakerpro.data.interfaces.UserViewModelVMI
import com.garudpuran.postermakerpro.models.FeedItem
import com.garudpuran.postermakerpro.models.UserPersonalProfileModel
import com.garudpuran.postermakerpro.utils.FirebaseStorageConstants
import com.garudpuran.postermakerpro.utils.ResponseStrings
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class HomeRepoImp(private val database: FirebaseFirestore,
                          private val storageReference: StorageReference
) : HomeRepo
{
        override suspend fun getAllFeedItems(): List<FeedItem>  = suspendCoroutine {
                continuation ->
            val db = database.collection(FirebaseStorageConstants.MAIN_FEED_NODE)
            try {
                val list = ArrayList<FeedItem>()
                db.get().addOnSuccessListener {
                    for (doc in it.documents) {
                        val data = doc.toObject(FeedItem::class.java)
                        list.add(data!!)
                    }
                    continuation.resume(list)
                }
            }catch (e: FirebaseFirestoreException){
                continuation.resume(emptyList())
            } catch (e:Exception){
                continuation.resume(emptyList())
            }

        }

    override suspend fun likeFeedItem(item: FeedItem): String? {
        try {
            val ctd = suspendCoroutine { ff ->
                val db = database.collection(FirebaseStorageConstants.MAIN_FEED_NODE)
                db.document(item.Id!!).set(item)
                    .addOnSuccessListener {
                        ff.resume(ResponseStrings.SUCCESS)
                    }.addOnFailureListener {
                        ff.resume(ResponseStrings.ERROR)
                    }

            }
            return ctd

        } catch (e: FirebaseFirestoreException) {
            return null
        } catch (e: Exception) {
            return null
        }

    }


}