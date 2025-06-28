package ch.seesturm.pfadiseesturm.di

import android.content.Context
import ch.seesturm.pfadiseesturm.data.auth.AuthApi
import ch.seesturm.pfadiseesturm.data.auth.AuthApiImpl
import ch.seesturm.pfadiseesturm.data.auth.repository.AuthRepositoryImpl
import ch.seesturm.pfadiseesturm.domain.auth.repository.AuthRepository
import ch.seesturm.pfadiseesturm.domain.auth.service.AuthService
import ch.seesturm.pfadiseesturm.domain.fcf.repository.CloudFunctionsRepository
import ch.seesturm.pfadiseesturm.domain.fcm.repository.FCMRepository
import ch.seesturm.pfadiseesturm.domain.firestore.repository.FirestoreRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

interface AuthModule {

    val authApi: AuthApi
    val authRepository: AuthRepository
    val authService: AuthService
}

class AuthModuleImpl(
    private val appContext: Context,
    private val cloudFunctionsRepository: CloudFunctionsRepository,
    private val firestoreRepository: FirestoreRepository,
    private val fcmRepository: FCMRepository,
    private val firebaseAuth: FirebaseAuth = Firebase.auth
): AuthModule {

    override val authApi: AuthApi by lazy {
        AuthApiImpl(
            context = appContext,
            firebaseAuth = firebaseAuth
        )
    }

    override val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(authApi)
    }

    override val authService: AuthService by lazy {
        AuthService(
            authRepository = authRepository,
            cloudFunctionsRepository = cloudFunctionsRepository,
            firestoreRepository = firestoreRepository,
            fcmRepository = fcmRepository
        )
    }
}