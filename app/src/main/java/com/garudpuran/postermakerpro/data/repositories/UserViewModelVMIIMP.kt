package com.garudpuran.postermakerpro.data.repositories

import com.garudpuran.postermakerpro.data.interfaces.UserViewModelVMI
import com.garudpuran.postermakerpro.models.UserPersonalProfileModel
import com.garudpuran.postermakerpro.utils.ResponseStrings
import com.garudpuran.postermakerpro.utils.UserReferences
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.StorageReference
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
}





