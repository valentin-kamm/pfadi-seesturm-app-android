package ch.seesturm.pfadiseesturm.data.wordpress.repository

import ch.seesturm.pfadiseesturm.util.MemoryCacheIdentifier
import ch.seesturm.pfadiseesturm.data.wordpress.WordpressApi
import ch.seesturm.pfadiseesturm.data.wordpress.dto.WordpressPostDto
import ch.seesturm.pfadiseesturm.data.wordpress.dto.WordpressPostsDto
import ch.seesturm.pfadiseesturm.domain.wordpress.repository.AktuellRepository

class AktuellRepositoryImpl (
    private val api: WordpressApi
) : AktuellRepository {

    override var postListMemoryCache: List<WordpressPostDto> = emptyList()
    override var latestPostMemoryCache: WordpressPostDto? = null

    override suspend fun getPosts(start: Int, length: Int): WordpressPostsDto {
        val response = api.getPosts(start, length)
        postListMemoryCache = response.posts
        return response
    }
    override suspend fun getMorePosts(start: Int, length: Int): WordpressPostsDto {
        val response = api.getPosts(start, length)
        postListMemoryCache = postListMemoryCache + response.posts
        return response
    }
    override suspend fun getPost(postId: Int, cacheIdentifier: MemoryCacheIdentifier): WordpressPostDto {
        return when (cacheIdentifier) {
            MemoryCacheIdentifier.Push -> {
                api.getPost(postId)
            }
            MemoryCacheIdentifier.List -> {
                postListMemoryCache.find { it.id == postId }
                    ?: api.getPost(postId)
            }
            MemoryCacheIdentifier.Home -> {
                latestPostMemoryCache?.takeIf { it.id == postId }
                    ?: api.getPost(postId)
            }
        }
    }
    override suspend fun getLatestPost(): WordpressPostDto {
        val post = api.getPosts(0, 1).posts.first()
        latestPostMemoryCache = post
        return post
    }
}