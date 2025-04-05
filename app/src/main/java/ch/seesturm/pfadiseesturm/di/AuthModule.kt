package ch.seesturm.pfadiseesturm.di

import android.content.Context
import ch.seesturm.pfadiseesturm.data.auth.AuthApi
import ch.seesturm.pfadiseesturm.data.auth.AuthApiImpl
import ch.seesturm.pfadiseesturm.data.auth.repository.AuthRepositoryImpl
import ch.seesturm.pfadiseesturm.domain.auth.repository.AuthRepository
import ch.seesturm.pfadiseesturm.domain.auth.service.AuthService
import ch.seesturm.pfadiseesturm.domain.fcf.repository.CloudFunctionsRepository
import ch.seesturm.pfadiseesturm.domain.firestore.repository.FirestoreRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

interface AuthModule {

    val appAuthApi: AuthApi
    val firebaseAuth: FirebaseAuth

    val authRepository: AuthRepository
    val authService: AuthService
}

class AuthModuleImpl(
    private val appContext: Context,
    private val cloudFunctionsRepository: CloudFunctionsRepository,
    private val firestoreRepository: FirestoreRepository
): AuthModule {

    override val firebaseAuth: FirebaseAuth
        get() = Firebase.auth

    override val appAuthApi: AuthApi by lazy {
        AuthApiImpl(
            context = appContext,
            firebaseAuth = firebaseAuth
        )
    }

    override val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(appAuthApi)
    }
    override val authService: AuthService by lazy {
        AuthService(
            repository = authRepository,
            cloudFunctionsRepository = cloudFunctionsRepository,
            firestoreRepository = firestoreRepository,
            firebaseAuth = firebaseAuth
        )
    }
}