package com.garudpuran.postermakerpro.data.repositories

import android.util.Log
import com.garudpuran.postermakerpro.data.interfaces.HomeRepo
import com.garudpuran.postermakerpro.models.CategoryItem
import com.garudpuran.postermakerpro.models.FeedItem
import com.garudpuran.postermakerpro.models.PostItem
import com.garudpuran.postermakerpro.models.RechargeItem
import com.garudpuran.postermakerpro.models.SubCategoryItem
import com.garudpuran.postermakerpro.models.TrendingStoriesItemModel
import com.garudpuran.postermakerpro.models.UserPersonalProfileModel
import com.garudpuran.postermakerpro.utils.FirebaseStorageConstants
import com.garudpuran.postermakerpro.utils.ResponseStrings
import com.garudpuran.postermakerpro.utils.UserReferences
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
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

    override suspend fun getAllTrendingStories(): List<TrendingStoriesItemModel>  = suspendCoroutine {
            continuation ->
        val db = database.collection(FirebaseStorageConstants.MAIN_TRENDING_POSTS_NODE)
        try {
            val list = ArrayList<TrendingStoriesItemModel>()
            db.get().addOnSuccessListener {
                for (doc in it.documents) {
                    val data = doc.toObject(TrendingStoriesItemModel::class.java)
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

    override suspend fun getAllCategoriesAndSubCategories(): List<Pair<CategoryItem, List<SubCategoryItem>>> {
        return withContext(Dispatchers.IO) {
            val db = database.collection(FirebaseStorageConstants.MAIN_CATEGORIES_NODE)

            try {
                val list = ArrayList<Pair<CategoryItem, List<SubCategoryItem>>>()
                val categoryQuerySnapshot = db.get().await()

                for (doc in categoryQuerySnapshot.documents) {
                    val data = doc.toObject(CategoryItem::class.java)

                    val subcollection = db.document(data?.Id!!).collection(FirebaseStorageConstants.MAIN_SUB_CATEGORIES_NODE)
                    val subQuerySnapshot = subcollection.get().await()

                    val subList = subQuerySnapshot.documents.mapNotNull { subDoc ->
                        subDoc.toObject(SubCategoryItem::class.java)
                    }

                    list.add(Pair(data, subList))
                }

                list
            } catch (e: FirebaseFirestoreException) {
                emptyList()
            } catch (e: Exception) {
                emptyList()
            }
    }
    }

    override suspend fun getAllPosts(catId: String, subCatId: String): List<PostItem>  = suspendCoroutine {
            continuation ->
        val db = database.collection(FirebaseStorageConstants.MAIN_CATEGORIES_NODE).document(catId).collection(FirebaseStorageConstants.MAIN_SUB_CATEGORIES_NODE).document(subCatId).collection(FirebaseStorageConstants.SUB_CATEGORIES_POSTS_NODE)
        try {
            val list = ArrayList<PostItem>()
            db.get().addOnSuccessListener {
                for (doc in it.documents) {
                    val data = doc.toObject(PostItem::class.java)
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

    override suspend fun getAllRecharges(): List<RechargeItem>  = suspendCoroutine {
            continuation ->
        val db = database.collection(FirebaseStorageConstants.MAIN_NODE_RCG)
        try {
            val list = ArrayList<RechargeItem>()
            db.get().addOnSuccessListener {
                for (doc in it.documents) {
                    val data = doc.toObject(RechargeItem::class.java)
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