package ch.seesturm.pfadiseesturm.data.data_store.dao

import ch.seesturm.pfadiseesturm.domain.data_store.model.GespeichertePerson
import kotlinx.serialization.Serializable

@Serializable
data class GespeichertePersonDao(
    val id: String,
    val vorname: String,
    val nachname: String,
    val pfadiname: String?
)

fun GespeichertePersonDao.toGespeichertePerson(): GespeichertePerson {
    return GespeichertePerson(
        id = id,
        vorname = vorname,
        nachname = nachname,
        pfadiname = pfadiname
    )
}
fun GespeichertePerson.toGespeichertePersonDao(): GespeichertePersonDao {
    return GespeichertePersonDao(
        id = id,
        vorname = vorname,
        nachname = nachname,
        pfadiname = pfadiname
    )
}