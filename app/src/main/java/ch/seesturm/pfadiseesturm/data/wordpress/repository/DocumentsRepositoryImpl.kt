package ch.seesturm.pfadiseesturm.data.wordpress.repository

import ch.seesturm.pfadiseesturm.data.wordpress.WordpressApi
import ch.seesturm.pfadiseesturm.data.wordpress.dto.WordpressDocumentDto
import ch.seesturm.pfadiseesturm.domain.wordpress.repository.DocumentsRepository
import ch.seesturm.pfadiseesturm.util.types.WordpressDocumentType

class DocumentsRepositoryImpl(
    private val api: WordpressApi
): DocumentsRepository {

    override suspend fun getDocuments(type: WordpressDocumentType): List<WordpressDocumentDto> {
        return when (type) {
            WordpressDocumentType.Documents -> {
                api.getDocuments()
            }
            WordpressDocumentType.Luuchtturm -> {
                api.getLuuchtturm()
            }
        }
    }
}