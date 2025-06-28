package ch.seesturm.pfadiseesturm.di

import ch.seesturm.pfadiseesturm.data.fcf.CloudFunctionsApi
import ch.seesturm.pfadiseesturm.data.fcf.CloudFunctionsApiImpl
import ch.seesturm.pfadiseesturm.data.fcf.repository.CloudFunctionsRepositoryImpl
import ch.seesturm.pfadiseesturm.domain.fcf.repository.CloudFunctionsRepository
import com.google.firebase.functions.FirebaseFunctions

interface FCFModule {

    val fcfApi: CloudFunctionsApi
    val fcfRepository: CloudFunctionsRepository
}

class FCFModuleImpl(
    private val functions: FirebaseFunctions = FirebaseFunctions.getInstance()
): FCFModule {

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