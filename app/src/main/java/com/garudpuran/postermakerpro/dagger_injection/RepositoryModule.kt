package com.garudpuran.postermakerpro.dagger_injection

import com.garudpuran.postermakerpro.data.interfaces.UserViewModelVMI
import com.garudpuran.postermakerpro.data.repositories.UserViewModelVMIIMP
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {
    @Provides
    @Singleton
    fun provideUserProfileImp(database:FirebaseFirestore,storageReference: StorageReference): UserViewModelVMI {
        return UserViewModelVMIIMP(database,storageReference)
    }



}