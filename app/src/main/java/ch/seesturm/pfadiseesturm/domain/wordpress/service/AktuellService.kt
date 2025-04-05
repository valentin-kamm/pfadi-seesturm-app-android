package ch.seesturm.pfadiseesturm.domain.wordpress.service

import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.MemoryCacheIdentifier
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import ch.seesturm.pfadiseesturm.data.wordpress.dto.toWordpressPost
import ch.seesturm.pfadiseesturm.data.wordpress.dto.toWordpressPosts
import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressPost
import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressPosts
import ch.seesturm.pfadiseesturm.domain.wordpress.repository.AktuellRepository

class AktuellService(
    private val repository: AktuellRepository
): WordpressService() {

    // function that fetches the desired list of posts
    suspend fun fetchPosts(start: Int, length: Int): SeesturmResult<WordpressPosts, DataError.Network> =
        fetchFromWordpress(
            fetchAction = { repository.getPosts(start, length) },
            transform = { it.toWordpressPosts() }
        )

    // function that fetches more posts when using the infinite scroll functionality
    suspend fun fetchMorePosts(start: Int, length: Int): SeesturmResult<WordpressPosts, DataError.Network> =
        fetchFromWordpress(
            fetchAction = { repository.getMorePosts(start, length) },
            transform = { it.toWordpressPosts() }
        )

    // function that obtains the desired post by its id, either by fetching it from the repository memory cache of from the api
    suspend fun getOrFetchPost(postId: Int, cacheIdentifier: MemoryCacheIdentifier): SeesturmResult<WordpressPost, DataError.Network> =
        fetchFromWordpress(
            fetchAction = { repository.getPost(postId, cacheIdentifier) },
            transform = { it.toWordpressPost() }
        )

    // function that gets the latest post from the api
    suspend fun fetchLatestPost(): SeesturmResult<WordpressPost, DataError.Network> =
        fetchFromWordpress(
            fetchAction = { repository.getLatestPost() },
            transform = { it.toWordpressPost() }
        )
}