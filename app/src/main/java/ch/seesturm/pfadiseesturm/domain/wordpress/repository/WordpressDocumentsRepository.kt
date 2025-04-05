package ch.seesturm.pfadiseesturm.domain.wordpress.repository

import ch.seesturm.pfadiseesturm.data.wordpress.dto.WordpressDocumentDto

interface WordpressDocumentsRepository {
    suspend fun getDocuments(): List<WordpressDocumentDto>
    suspend fun getLuuchtturm(): List<WordpressDocumentDto>
}