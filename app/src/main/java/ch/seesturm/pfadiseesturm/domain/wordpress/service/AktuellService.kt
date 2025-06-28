package ch.seesturm.pfadiseesturm.domain.wordpress.service

import ch.seesturm.pfadiseesturm.data.wordpress.dto.toWordpressPost
import ch.seesturm.pfadiseesturm.data.wordpress.dto.toWordpressPosts
import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressPost
import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressPosts
import ch.seesturm.pfadiseesturm.domain.wordpress.repository.AktuellRepository
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.types.MemoryCacheIdentifier
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult

class AktuellService(
    private val repository: AktuellRepository
): WordpressService() {

    suspend fun fetchPosts(start: Int, length: Int): SeesturmResult<WordpressPosts, DataError.Network> =
        fetchFromWordpress(
            fetchAction = { repository.getPosts(start, length) },
            transform = { it.toWordpressPosts() }
        )

    suspend fun fetchMorePosts(start: Int, length: Int): SeesturmResult<WordpressPosts, DataError.Network> =
        fetchFromWordpress(
            fetchAction = { repository.getMorePosts(start, length) },
            transform = { it.toWordpressPosts() }
        )

    suspend fun getOrFetchPost(postId: Int, cacheIdentifier: MemoryCacheIdentifier): SeesturmResult<WordpressPost, DataError.Network> =
        fetchFromWordpress(
            fetchAction = { repository.getPost(postId, cacheIdentifier) },
            transform = { it.toWordpressPost() }
        )

    suspend fun fetchLatestPost(): SeesturmResult<WordpressPost, DataError.Network> =
        fetchFromWordpress(
            fetchAction = { repository.getLatestPost() },
            transform = { it.toWordpressPost() }
        )
}