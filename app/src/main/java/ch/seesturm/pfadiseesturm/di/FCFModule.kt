package ch.seesturm.pfadiseesturm.di

import android.content.Context
import ch.seesturm.pfadiseesturm.data.fcf.CloudFunctionsApi
import ch.seesturm.pfadiseesturm.data.fcf.CloudFunctionsApiImpl
import ch.seesturm.pfadiseesturm.data.fcf.repository.CloudFunctionsRepositoryImpl
import ch.seesturm.pfadiseesturm.domain.fcf.repository.CloudFunctionsRepository
import com.google.firebase.functions.FirebaseFunctions

interface FCFModule {

    val functions: FirebaseFunctions

    val fcfApi: CloudFunctionsApi
    val fcfRepository: CloudFunctionsRepository
}

class FCFModuleImpl(
    private val appContext: Context
): FCFModule {

    override val functions: FirebaseFunctions by lazy {
        FirebaseFunctions.getInstance()
    }

    override val fcfApi: CloudFunctionsApi by lazy {
        CloudFunctionsApiImpl(
            functions = functions
        )
    }
    override val fcfRepository: CloudFunctionsRepository by lazy {
        CloudFunctionsRepositoryImpl(
            api = fcfApi
        )
    }
}