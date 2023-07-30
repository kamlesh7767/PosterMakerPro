package com.garudpuran.postermakerpro.data.repositories

import android.net.Uri
import com.garudpuran.postermakerpro.data.interfaces.UserViewModelVMI
import com.garudpuran.postermakerpro.ui.commonui.models.ProfileItemModel
import com.garudpuran.postermakerpro.utils.UiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.StorageReference


class UserViewModelVMIIMP(private val database: FirebaseFirestore,
                        private val storageReference: StorageReference
) : UserViewModelVMI
{
    override suspend fun updateProfile(id: String,mod:ProfileItemModel, onResult: (UiState<String>) -> Unit) {

        try{
            val db =  database.collection("users").document(id).collection("user_docs").document(
                "profile")
            db.set(mod).addOnSuccessListener {
                onResult.invoke(UiState.Success("Updated"))
            }
        }catch (e: FirebaseFirestoreException){
            onResult.invoke(UiState.Failure(e.message))
        } catch (e:Exception){
            onResult.invoke(UiState.Failure(e.message))
        }
    }
}





