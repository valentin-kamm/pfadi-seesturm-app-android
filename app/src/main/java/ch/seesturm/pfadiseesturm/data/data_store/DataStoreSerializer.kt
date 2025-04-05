package ch.seesturm.pfadiseesturm.data.data_store

import androidx.datastore.core.Serializer
import ch.seesturm.pfadiseesturm.data.data_store.dao.SeesturmPreferencesDao
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object DataStoreSerializer: Serializer<SeesturmPreferencesDao> {

    override val defaultValue: SeesturmPreferencesDao
        get() = SeesturmPreferencesDao()

    override suspend fun readFrom(input: InputStream): SeesturmPreferencesDao {
        return Json.decodeFromString(
            deserializer = SeesturmPreferencesDao.serializer(),
            string = input.readBytes().decodeToString()
        )
    }
    override suspend fun writeTo(t: SeesturmPreferencesDao, output: OutputStream) {
        output.run {
            write(
                Json.encodeToString(
                    serializer = SeesturmPreferencesDao.serializer(),
                    value = t
                ).encodeToByteArray()
            )
        }
    }
}