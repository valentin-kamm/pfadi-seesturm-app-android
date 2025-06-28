package ch.seesturm.pfadiseesturm.domain.wordpress.repository

import ch.seesturm.pfadiseesturm.data.wordpress.dto.WordpressPostDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.WordpressPostsDto
import ch.seesturm.pfadiseesturm.util.types.MemoryCacheIdentifier

interface AktuellRepository {

    var postListMemoryCache: List<WordpressPostDto>
    var latestPostMemoryCache: WordpressPostDto?
    suspend fun getPosts(start: Int, length: Int): WordpressPostsDto
    suspend fun getMorePosts(start: Int, length: Int): WordpressPostsDto
    suspend fun getPost(postId: Int, cacheIdentifier: MemoryCacheIdentifier): WordpressPostDto
    suspend fun getLatestPost(): WordpressPostDto
}