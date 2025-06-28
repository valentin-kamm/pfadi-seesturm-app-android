package ch.seesturm.pfadiseesturm.domain.wordpress.repository

import ch.seesturm.pfadiseesturm.data.wordpress.dto.WordpressDocumentDto
import ch.seesturm.pfadiseesturm.util.types.WordpressDocumentType

interface DocumentsRepository {
    suspend fun getDocuments(type: WordpressDocumentType): List<WordpressDocumentDto>
}