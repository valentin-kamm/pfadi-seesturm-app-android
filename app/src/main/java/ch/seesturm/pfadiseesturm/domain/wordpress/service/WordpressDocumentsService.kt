package ch.seesturm.pfadiseesturm.domain.wordpress.service

import ch.seesturm.pfadiseesturm.data.wordpress.dto.toWordpressDocument
import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressDocument
import ch.seesturm.pfadiseesturm.domain.wordpress.repository.WordpressDocumentsRepository
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult

class WordpressDocumentsService(
    private val repository: WordpressDocumentsRepository
): WordpressService() {

    suspend fun getDocuments(): SeesturmResult<List<WordpressDocument>, DataError.Network> =
        fetchFromWordpress(
            fetchAction = { repository.getDocuments() },
            transform = { it.map { it.toWordpressDocument() }}
        )
    suspend fun getLuuchtturm(): SeesturmResult<List<WordpressDocument>, DataError.Network> =
        fetchFromWordpress(
            fetchAction = { repository.getLuuchtturm() },
            transform = { it.map { it.toWordpressDocument() }}
        )
}