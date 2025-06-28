package ch.seesturm.pfadiseesturm.domain.wordpress.service

import ch.seesturm.pfadiseesturm.data.wordpress.dto.toWordpressDocument
import ch.seesturm.pfadiseesturm.domain.wordpress.model.WordpressDocument
import ch.seesturm.pfadiseesturm.domain.wordpress.repository.DocumentsRepository
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.types.WordpressDocumentType
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult

class DocumentsService(
    private val repository: DocumentsRepository
): WordpressService() {

    suspend fun getDocuments(type: WordpressDocumentType): SeesturmResult<List<WordpressDocument>, DataError.Network> =
        fetchFromWordpress(
            fetchAction = { repository.getDocuments(type) },
            transform = { list ->
                list.map { document ->
                    document.toWordpressDocument()
                }
            }
        )
}