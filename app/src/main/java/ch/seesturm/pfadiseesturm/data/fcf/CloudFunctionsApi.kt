package ch.seesturm.pfadiseesturm.data.fcf

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.google.firebase.functions.FirebaseFunctions
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

interface CloudFunctionsApi {

    val functions: FirebaseFunctions

    suspend fun <I: Any, O: Any>invokeCloudFunction(
        function: SeesturmCloudFunction,
        data: I? = null,
        inputSerializer: KSerializer<I>,
        outputSerializer: KSerializer<O>
    ): O
}

class CloudFunctionsApiImpl(
    override val functions: FirebaseFunctions
): CloudFunctionsApi {

    private val gson = Gson()

    override suspend fun <I : Any, O : Any> invokeCloudFunction(
        function: SeesturmCloudFunction,
        data: I?,
        inputSerializer: KSerializer<I>,
        outputSerializer: KSerializer<O>
    ): O {

        val inputData = data?.let {
            val jsonString = Json.encodeToString(inputSerializer, it)
            gson.fromJson(jsonString, Any::class.java)
        }

        val result = functions.getHttpsCallable(function.functionName)
            .call(inputData)
            .await()

        val resultJson = gson.toJson(result.data)
        return Json.decodeFromString(outputSerializer, resultJson)
    }
}

enum class SeesturmCloudFunction(
    val functionName: String
) {
    GetFirebaseAuthToken(functionName = "getfirebaseauthtokenv2"),
    PublishGoogleCalendarEvent(functionName = "addcalendareventv2"),
    UpdateGoogleCalendarEvent(functionName = "updatecalendareventv2")
}