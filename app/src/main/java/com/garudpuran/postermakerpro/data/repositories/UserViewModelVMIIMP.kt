package com.garudpuran.postermakerpro.data.repositories

import android.net.Uri
import android.util.Log
import com.garudpuran.postermakerpro.data.interfaces.UserViewModelVMI
import com.garudpuran.postermakerpro.models.FeedItem
import com.garudpuran.postermakerpro.models.PostItem
import com.garudpuran.postermakerpro.models.RechargeItem
import com.garudpuran.postermakerpro.models.UserPersonalProfileModel
import com.garudpuran.postermakerpro.models.UserProfessionalProfileModel
import com.garudpuran.postermakerpro.utils.FirebaseStorageConstants
import com.garudpuran.postermakerpro.utils.ResponseStrings
import com.garudpuran.postermakerpro.utils.UserReferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class UserViewModelVMIIMP(private val database: FirebaseFirestore,
                        private val storageReference: StorageReference
) : UserViewModelVMI
{
    override suspend fun updateProfile(
        id: String,
        mod: UserPersonalProfileModel
    ): String = suspendCoroutine { continuation ->
        try {
            val db = database.collection(UserReferences.USER_MAIN_NODE)
                .document(id)
            db.set(mod).addOnSuccessListener {
                continuation.resume(ResponseStrings.SUCCESS)
            }.addOnFailureListener { e ->
                continuation.resumeWithException(e)
            }
        } catch (e: FirebaseFirestoreException) {
            continuation.resume(ResponseStrings.ERROR)
        } catch (e: Exception) {
            continuation.resume(ResponseStrings.ERROR)
        }
    }

    override suspend fun updateProfileFields(id: String, paramMap: Map<String, Any>): String = suspendCoroutine { continuation ->
        try {
            val db = database.collection(UserReferences.USER_MAIN_NODE)
                .document(id)
            db.set(paramMap).addOnSuccessListener {
                continuation.resume(ResponseStrings.SUCCESS)
            }.addOnFailureListener { e ->
                continuation.resumeWithException(e)
            }
        } catch (e: FirebaseFirestoreException) {
            continuation.resume(ResponseStrings.ERROR)
        } catch (e: Exception) {
            continuation.resume(ResponseStrings.ERROR)
        }
    }

    override suspend fun getUserProfile(id: String): UserPersonalProfileModel? = suspendCoroutine {
        continuation ->
        val db =  database.collection(UserReferences.USER_MAIN_NODE).document(id)
        try {
            db.get().addOnSuccessListener {
                if(it.exists()){
                    val profile = it.toObject(UserPersonalProfileModel::class.java)
                    continuation.resume(profile!!)
                }else{
                    continuation.resume(null)
                }
            }.addOnFailureListener {
                continuation.resume(null)
            }
        } catch (e: FirebaseFirestoreException) {
            continuation.resume(null)
        } catch (e: Exception) {
            continuation.resume(null)
        }
    }


    override suspend fun getRechargeItem(id: String): RechargeItem? = suspendCoroutine {
            continuation ->
        val db =  database.collection(FirebaseStorageConstants.MAIN_NODE_RCG).document(id)
        try {
            db.get().addOnSuccessListener {
                if(it.exists()){
                    val profile = it.toObject(RechargeItem::class.java)
                    continuation.resume(profile!!)
                }else{
                    continuation.resume(null)
                }
            }.addOnFailureListener {
                continuation.resume(null)
            }
        } catch (e: FirebaseFirestoreException) {
            continuation.resume(null)
        } catch (e: Exception) {
            continuation.resume(null)
        }
    }


    override suspend fun uploadFeedPostItem(imageUri: Uri?,item: FeedItem): String? {
        try {
            val uri: Uri = withContext(Dispatchers.IO) {
                val title = imageUri!!.lastPathSegment + System.currentTimeMillis().toString()
                storageReference.child(FirebaseStorageConstants.MAIN_FEED_NODE).child(title)
                    .putFile(imageUri)
                    .await()
                    .storage
                    .downloadUrl
                    .await()
            }
            val ctd = suspendCoroutine { ff ->

                val db = database.collection(FirebaseStorageConstants.MAIN_FEED_NODE)
                val key = db.document().id
                item.Id = key
                item.image_url = uri.toString()
                db.document(key).set(item)
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

    override suspend fun updatePersonalProfileItem(imageUri: String, item: UserPersonalProfileModel):String? {
        try {
            var uri: String
            if(imageUri.isNotEmpty()){
                uri = withContext(Dispatchers.IO) {
                    val parsedUri = Uri.parse(imageUri)
                    val title = parsedUri.lastPathSegment + System.currentTimeMillis().toString()
                    storageReference.child(FirebaseStorageConstants.MAIN_USER_DP).child(title)
                        .putFile(parsedUri)
                        .await()
                        .storage
                        .downloadUrl
                        .await()
                }  .toString()
                if (item.profile_image_url.isNotEmpty()) {
                    val ref = FirebaseStorage.getInstance()
                    ref.getReferenceFromUrl(item.profile_image_url).delete()
                }
            }else{
                uri = item.profile_image_url
            }
            item.profile_image_url = uri
            val ctd = suspendCoroutine { ff ->
                val db = database.collection(UserReferences.USER_MAIN_NODE)
                db.document(item.uid).set(item)
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
            Log.d("IMAGEUPLOAD",e.toString())
            return null
        }
    }


    override suspend fun updateProfessionalProfileItem(imageUri: String, item: UserProfessionalProfileModel):String? {
        try {
            val auth= FirebaseAuth.getInstance()
               val uri = withContext(Dispatchers.IO) {
                    val parsedUri = Uri.parse(imageUri)
                    val title = parsedUri.lastPathSegment + System.currentTimeMillis().toString()
                    storageReference.child(FirebaseStorageConstants.MAIN_USER_DP).child(title)
                        .putFile(parsedUri)
                        .await()
                        .storage
                        .downloadUrl
                        .await()
                }  .toString()

            item.logo_image_url = uri
            val ctd = suspendCoroutine { ff ->
                val db = database.collection(UserReferences.USER_MAIN_NODE).document(auth.uid!!).collection(UserReferences.USER_PROFESSIONAL_PROFILES)
                val key = db.document().id
                item.id = key
                db.document(key).set(item)
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
            Log.d("IMAGEUPLOAD",e.toString())
            return null
        }
    }

    override suspend fun getAllProfessionalProfileItemsAsync(): List<UserProfessionalProfileModel>  = suspendCoroutine {
            continuation ->
        val auth= FirebaseAuth.getInstance()
        val db = database.collection(UserReferences.USER_MAIN_NODE).document(auth.uid!!).collection(UserReferences.USER_PROFESSIONAL_PROFILES)
        try {
            val list = ArrayList<UserProfessionalProfileModel>()
            db.get().addOnSuccessListener {
                for (doc in it.documents) {
                    val data = doc.toObject(UserProfessionalProfileModel::class.java)
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

}





