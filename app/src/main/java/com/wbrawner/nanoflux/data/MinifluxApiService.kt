package com.wbrawner.nanoflux.data

import com.wbrawner.nanoflux.data.model.Category
import com.wbrawner.nanoflux.data.model.Entry
import com.wbrawner.nanoflux.data.model.Feed
import com.wbrawner.nanoflux.data.model.User
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.http.*

interface MinifluxApiService {
//    @POST("discover")
//    suspend fun discoverSubscriptions(@Body url: String): DiscoverResponse

    @GET("feeds")
    suspend fun getFeeds(): List<Feed>

    @GET("categories/{categoryId}/feeds")
    suspend fun getCategoryFeeds(@Path("categoryId") categoryId: Long): List<Feed>

    @GET("feeds/{id}")
    suspend fun getFeed(@Path("id") id: Long): Feed

    @GET("feeds/{feedId}/icon")
    suspend fun getFeedIcon(@Path("feedId") feedId: Long): Feed.Icon

//    @POST("feeds")
//    suspend fun createFeed(feed: FeedRequest): CreateFeedResponse

//    @PUT("feeds/{id}")
//    suspend fun updateFeed(@Path("id") id: Long, @Body request: FeedRequest): Feed

    @PUT("feeds/refresh")
    suspend fun refreshFeeds(): Response

    @PUT("feeds/{id}/refresh")
    suspend fun refreshFeed(@Path("id") id: Long): Response

    @DELETE("feeds/{id}")
    suspend fun deleteFeed(@Path("id") id: Long): Response

    @GET("feeds/{feedId}/entries")
    suspend fun getFeedEntries(
        @Path("feedId") feedId: Long,
        @Query("status") status: List<Entry.Status>? = null,
        @Query("offset") offset: Long? = null,
        @Query("limit") limit: Long? = null,
        @Query("order") order: Entry.Order? = null,
        @Query("direction") direction: Entry.SortDirection? = null,
        @Query("before") before: Long? = null,
        @Query("after") after: Long? = null,
        @Query("before_entry_id") beforeEntryId: Long? = null,
        @Query("after_entry_id") afterEntryId: Long? = null,
        @Query("starred") starred: Boolean? = null,
        @Query("search") search: String? = null,
        @Query("category_id") categoryId: Long? = null,
    ): List<Entry>

    @GET("feeds/{feedId}/entries/{entryId}")
    suspend fun getFeedEntry(@Path("feedId") feedId: Long, @Path("entryId") entryId: Long): Entry

    @GET("entries")
    suspend fun getEntries(
        @Query("status") status: List<Entry.Status>? = null,
        @Query("offset") offset: Long? = null,
        @Query("limit") limit: Long? = null,
        @Query("order") order: Entry.Order? = null,
        @Query("direction") direction: Entry.SortDirection? = null,
        @Query("before") before: Long? = null,
        @Query("after") after: Long? = null,
        @Query("before_entry_id") beforeEntryId: Long? = null,
        @Query("after_entry_id") afterEntryId: Long? = null,
        @Query("starred") starred: Boolean? = null,
        @Query("search") search: String? = null,
        @Query("category_id") categoryId: Long? = null,
    ): List<Entry>

    @GET("entries/{entryId}")
    suspend fun getEntry(@Path("entryId") entryId: Long): Entry

    @PUT("feeds/{id}/mark-all-as-read")
    suspend fun markFeedEntriesAsRead(@Path("id") id: Long): Response

    //{
    //    "entry_ids": [1234, 4567],
    //    "status": "read"
    //}
//    @PUT("entries")
//    suspend fun updateEntries(request: UpdateEntriesRequest): Response

    @PUT("entries/{id}/bookmark")
    suspend fun toggleEntryBookmark(@Path("id") id: Long): Response

    @GET("categories")
    suspend fun getCategories(): List<Category>

    @POST("categories")
    suspend fun createCategory(@Body title: String): Category

    @PUT("categories/{id}")
    suspend fun updateCategory(@Path("id") id: Long, @Body title: String): Category

    @DELETE("categories/{id}")
    suspend fun deleteCategory(@Path("id") id: Long): Response

    @PUT("categories/{id}/mark-all-as-read")
    suspend fun markCategoryEntriesAsRead(@Path("id") id: Long): Response

    @GET("export")
    @Streaming
    suspend fun opmlExport(): retrofit2.Response<ResponseBody>

    @POST("import")
    @Streaming
    suspend fun opmlImport(@Body opml: RequestBody): retrofit2.Response<ResponseBody>

//    @POST("users")
//    suspend fun createUser(@Body user: UserRequest): User

    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: Long, @Body user: User): User

    @GET("me")
    suspend fun getCurrentUser(): User

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Long): User

    @GET("users/{name}")
    suspend fun getUserByName(@Path("name") name: String): User

    @GET("users")
    suspend fun getUses(): List<User>

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: Long)

    @PUT("users/{id}/mark-all-as-read")
    suspend fun markUserEntriesAsRead(@Path("id") id: Long): Response

    @GET("/healthcheck")
    suspend fun healthcheck(): retrofit2.Response<String>

    @GET("/version")
    suspend fun version(): retrofit2.Response<String>
}