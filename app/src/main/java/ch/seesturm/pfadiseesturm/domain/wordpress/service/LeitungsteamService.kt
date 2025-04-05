package ch.seesturm.pfadiseesturm.domain.wordpress.service

import ch.seesturm.pfadiseesturm.data.wordpress.dto.toLeitungsteam
import ch.seesturm.pfadiseesturm.domain.wordpress.model.Leitungsteam
import ch.seesturm.pfadiseesturm.domain.wordpress.repository.LeitungsteamRepository
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult

class LeitungsteamService(
    private val repository: LeitungsteamRepository
): WordpressService() {

    suspend fun fetchLeitungsteam(): SeesturmResult<List<Leitungsteam>, DataError.Network> =
        fetchFromWordpress(
            fetchAction = { repository.getLeitungsteam() },
            transform = { it.map { it.toLeitungsteam() } }
        )
}