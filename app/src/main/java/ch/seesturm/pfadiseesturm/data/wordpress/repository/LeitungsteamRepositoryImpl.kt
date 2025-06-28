package ch.seesturm.pfadiseesturm.data.wordpress.repository

import ch.seesturm.pfadiseesturm.data.wordpress.WordpressApi
import ch.seesturm.pfadiseesturm.data.wordpress.dto.LeitungsteamDto
import ch.seesturm.pfadiseesturm.domain.wordpress.repository.LeitungsteamRepository

class LeitungsteamRepositoryImpl(
    private val api: WordpressApi
): LeitungsteamRepository {

    override suspend fun getLeitungsteam(): List<LeitungsteamDto> =
        api.getLeitungsteam()
}