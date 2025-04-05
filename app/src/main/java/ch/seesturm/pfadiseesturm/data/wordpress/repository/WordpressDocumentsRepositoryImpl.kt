package ch.seesturm.pfadiseesturm.data.wordpress.repository

import ch.seesturm.pfadiseesturm.data.wordpress.WordpressApi
import ch.seesturm.pfadiseesturm.data.wordpress.dto.WordpressDocumentDto
import ch.seesturm.pfadiseesturm.domain.wordpress.repository.WordpressDocumentsRepository

class WordpressDocumentsRepositoryImpl(
    private val api: WordpressApi
): WordpressDocumentsRepository {

    override suspend fun getDocuments(): List<WordpressDocumentDto> {
        return api.getDocuments()
    }
    override suspend fun getLuuchtturm(): List<WordpressDocumentDto> {
        return api.getLuuchtturm()
    }
}