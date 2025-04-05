package ch.seesturm.pfadiseesturm.domain.wordpress.repository

import ch.seesturm.pfadiseesturm.data.wordpress.dto.LeitungsteamDto

interface LeitungsteamRepository {
    suspend fun getLeitungsteam(): List<LeitungsteamDto>
}