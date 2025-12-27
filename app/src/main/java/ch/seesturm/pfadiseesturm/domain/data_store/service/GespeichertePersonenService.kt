package ch.seesturm.pfadiseesturm.domain.data_store.service

import ch.seesturm.pfadiseesturm.data.data_store.dao.toGespeichertePerson
import ch.seesturm.pfadiseesturm.data.data_store.dao.toGespeichertePersonDao
import ch.seesturm.pfadiseesturm.domain.data_store.model.GespeichertePerson
import ch.seesturm.pfadiseesturm.domain.data_store.repository.GespeichertePersonenRepository
import ch.seesturm.pfadiseesturm.util.DataError
import ch.seesturm.pfadiseesturm.util.PfadiSeesturmError
import ch.seesturm.pfadiseesturm.util.state.SeesturmResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerializationException

class GespeichertePersonenService(
    private val repository: GespeichertePersonenRepository
) {

    fun readPersons(): Flow<SeesturmResult<List<GespeichertePerson>, DataError.Local>> =
        repository.readPersons()
            .map<_, SeesturmResult<List<GespeichertePerson>, DataError.Local>> { list ->
                SeesturmResult.Success(list.map { it.toGespeichertePerson() })
            }
            .catch { e ->
                emit(
                    SeesturmResult.Error(
                        when (e) {
                            is SerializationException -> {
                                DataError.Local.READING_ERROR
                            }
                            is PfadiSeesturmError -> {
                                when (e) {
                                    is PfadiSeesturmError.InvalidFormInput -> DataError.Local.INVALID_FORM_INPUT
                                    is PfadiSeesturmError.DateError -> DataError.Local.INVALID_DATE
                                    else -> {
                                        DataError.Local.UNKNOWN
                                    }
                                }
                            }
                            else -> {
                                DataError.Local.UNKNOWN
                            }
                        }
                    )
                )
            }

    suspend fun insertPerson(newPerson: GespeichertePerson): SeesturmResult<Unit, DataError.Local> =
        try {
            repository.insertPerson(newPerson.toGespeichertePersonDao())
            SeesturmResult.Success(Unit)
        }
        catch (e: Exception) {
            SeesturmResult.Error(
                when (e) {
                    is SerializationException -> {
                        DataError.Local.SAVING_ERROR
                    }
                    is PfadiSeesturmError -> {
                        when (e) {
                            is PfadiSeesturmError.InvalidFormInput -> DataError.Local.INVALID_FORM_INPUT
                            is PfadiSeesturmError.DateError -> DataError.Local.INVALID_DATE
                            else -> {
                                DataError.Local.UNKNOWN
                            }
                        }
                    }
                    else -> {
                        DataError.Local.UNKNOWN
                    }
                }
            )
        }

    suspend fun deletePerson(id: String): SeesturmResult<Unit, DataError.Local> =
        try {
            repository.deletePerson(id)
            SeesturmResult.Success(Unit)
        }
        catch (e: Exception) {
            SeesturmResult.Error(
                when (e) {
                    is SerializationException -> {
                        DataError.Local.DELETING_ERROR
                    }
                    is PfadiSeesturmError -> {
                        when (e) {
                            is PfadiSeesturmError.InvalidFormInput -> DataError.Local.INVALID_FORM_INPUT
                            is PfadiSeesturmError.DateError -> DataError.Local.INVALID_DATE
                            else -> {
                                DataError.Local.UNKNOWN
                            }
                        }
                    }
                    else -> {
                        DataError.Local.UNKNOWN
                    }
                }
            )
        }
}
