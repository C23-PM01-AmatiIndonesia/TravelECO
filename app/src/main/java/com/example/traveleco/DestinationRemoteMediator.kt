//package com.example.traveleco
//
//import androidx.paging.LoadType
//import androidx.paging.PagingState
//import androidx.paging.RemoteMediator
//
//class DestinationRemoteMediator(
//    private val apiService: ApiService,
//    private val storyDatabase: StoryDatabase,
//    private val authPreference: AuthPreference
//): RemoteMediator<Int, Story>() {
//
//    override suspend fun initialize(): InitializeAction {
//        return InitializeAction.LAUNCH_INITIAL_REFRESH
//    }
//
//    override suspend fun load(loadType: LoadType, state: PagingState<Int, Story>): MediatorResult {
//        val token = "Bearer ${authPreference.getAuthToken().first().token}"
//        val page = when (loadType) {
//            LoadType.REFRESH -> {
//                val remoteKeys = getRemoteKetClosestToCurrentPosition(state)
//                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
//            }
//            LoadType.PREPEND -> {
//                val remoteKeys = getRemoteKeyForFirstItem(state)
//                val prefKey = remoteKeys?.prevKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
//                prefKey
//            }
//            LoadType.APPEND -> {
//                val remoteKeys = getRemoteKeyForLastItem(state)
//                val nextKey = remoteKeys?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
//                nextKey
//            }
//        }
//
//        try {
//            val response = apiService.getAllStories(token, page, state.config.pageSize).listStory
//            val endOfPaginationReached = response.isEmpty()
//
//            storyDatabase.withTransaction {
//                if (loadType == LoadType.REFRESH) {
//                    storyDatabase.remoteKeysDao().deleteRemoteKeys()
//                    storyDatabase.StoryDao().deleteAll()
//                }
//                val prevKey = if (page == 1) null else page - 1
//                val nextKey = if (endOfPaginationReached) null else page + 1
//                val keys = response.map {
//                    RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
//                }
//                storyDatabase.remoteKeysDao().insertAll(keys)
//                storyDatabase.StoryDao().insertStory(response)
//            }
//            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
//        } catch (e: Exception) {
//            return MediatorResult.Error(e)
//        }
//    }
//
//    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Story>): RemoteKeys? {
//        return state.pages.lastOrNull {it.data.isNotEmpty()}?.data?.lastOrNull()?.let { data ->
//            storyDatabase.remoteKeysDao().getRemoteKeysId(data.id)
//        }
//    }
//
//    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Story>): RemoteKeys? {
//        return state.pages.firstOrNull {it.data.isNotEmpty()}?.data?.firstOrNull()?.let { data ->
//            storyDatabase.remoteKeysDao().getRemoteKeysId(data.id)
//        }
//    }
//
//    private suspend fun getRemoteKetClosestToCurrentPosition(state: PagingState<Int, Story>): RemoteKeys? {
//        return state.anchorPosition?.let { position ->
//            state.closestItemToPosition(position)?.id?.let { id ->
//                storyDatabase.remoteKeysDao().getRemoteKeysId(id)
//            }
//        }
//    }
//
//    private companion object {
//        const val INITIAL_PAGE_INDEX = 1
//    }
//}