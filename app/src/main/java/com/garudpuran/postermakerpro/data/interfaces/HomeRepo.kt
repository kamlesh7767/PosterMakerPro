package com.garudpuran.postermakerpro.data.interfaces

import com.garudpuran.postermakerpro.models.FeedItem
import com.garudpuran.postermakerpro.models.UserPersonalProfileModel

interface HomeRepo {
    suspend fun getAllFeedItems():List<FeedItem>
    suspend fun likeFeedItem(item: FeedItem):String?

}