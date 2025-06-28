package ch.seesturm.pfadiseesturm.di

import ch.seesturm.pfadiseesturm.data.wordpress.WordpressApi
import ch.seesturm.pfadiseesturm.data.wordpress.WordpressApiImpl
import ch.seesturm.pfadiseesturm.data.wordpress.WordpressBaseApi
import ch.seesturm.pfadiseesturm.data.wordpress.repository.AktuellRepositoryImpl
import ch.seesturm.pfadiseesturm.data.wordpress.repository.AnlaesseRepositoryImpl
import ch.seesturm.pfadiseesturm.data.wordpress.repository.DocumentsRepositoryImpl
import ch.seesturm.pfadiseesturm.data.wordpress.repository.LeitungsteamRepositoryImpl
import ch.seesturm.pfadiseesturm.data.wordpress.repository.NaechsteAktivitaetRepositoryImpl
import ch.seesturm.pfadiseesturm.data.wordpress.repository.PhotosRepositoryImpl
import ch.seesturm.pfadiseesturm.data.wordpress.repository.WeatherRepositoryImpl
import ch.seesturm.pfadiseesturm.domain.data_store.repository.SelectedStufenRepository
import ch.seesturm.pfadiseesturm.domain.firestore.repository.FirestoreRepository
import ch.seesturm.pfadiseesturm.domain.wordpress.repository.AktuellRepository
import ch.seesturm.pfadiseesturm.domain.wordpress.repository.AnlaesseRepository
import ch.seesturm.pfadiseesturm.domain.wordpress.repository.DocumentsRepository
import ch.seesturm.pfadiseesturm.domain.wordpress.repository.LeitungsteamRepository
import ch.seesturm.pfadiseesturm.domain.wordpress.repository.NaechsteAktivitaetRepository
import ch.seesturm.pfadiseesturm.domain.wordpress.repository.PhotosRepository
import ch.seesturm.pfadiseesturm.domain.wordpress.repository.WeatherRepository
import ch.seesturm.pfadiseesturm.domain.wordpress.service.AktuellService
import ch.seesturm.pfadiseesturm.domain.wordpress.service.AnlaesseService
import ch.seesturm.pfadiseesturm.domain.wordpress.service.DocumentsService
import ch.seesturm.pfadiseesturm.domain.wordpress.service.LeitungsteamService
import ch.seesturm.pfadiseesturm.domain.wordpress.service.NaechsteAktivitaetService
import ch.seesturm.pfadiseesturm.domain.wordpress.service.PhotosService
import ch.seesturm.pfadiseesturm.domain.wordpress.service.WeatherService
import ch.seesturm.pfadiseesturm.util.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface WordpressModule {
    val wordpressBaseApi: WordpressBaseApi
    val wordpressApi: WordpressApi

    val aktuellRepository: AktuellRepository
    val aktuellService: AktuellService

    val anlaesseRepository: AnlaesseRepository
    val anlaesseService: AnlaesseService

    val weatherRepository: WeatherRepository
    val weatherService: WeatherService

    val photosRepository: PhotosRepository
    val photosService: PhotosService

    val documentsRepository: DocumentsRepository
    val documentsService: DocumentsService

    val leitungsteamRepository: LeitungsteamRepository
    val leitungsteamService: LeitungsteamService

    val naechsteAktivitaetRepository: NaechsteAktivitaetRepository
    val naechsteAktivitaetService: NaechsteAktivitaetService
}

class WordpressModuleImpl(
    private val firestoreRepository: FirestoreRepository,
    selectedStufenRepository: SelectedStufenRepository
) : WordpressModule {

    override val wordpressBaseApi: WordpressBaseApi by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.WORDPRESS_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WordpressBaseApi::class.java)
    }

    override val wordpressApi: WordpressApi by lazy {
        WordpressApiImpl(wordpressBaseApi)
    }

    override val aktuellRepository: AktuellRepository by lazy {
        AktuellRepositoryImpl(wordpressApi)
    }
    override val aktuellService: AktuellService by lazy {
        AktuellService(aktuellRepository)
    }

    override val anlaesseRepository: AnlaesseRepository by lazy {
        AnlaesseRepositoryImpl(wordpressApi)
    }
    override val anlaesseService: AnlaesseService by lazy {
        AnlaesseService(anlaesseRepository)
    }

    override val weatherRepository: WeatherRepository by lazy {
        WeatherRepositoryImpl(wordpressApi)
    }
    override val weatherService: WeatherService by lazy {
        WeatherService(weatherRepository)
    }

    override val photosRepository: PhotosRepository by lazy {
        PhotosRepositoryImpl(wordpressApi)
    }
    override val photosService: PhotosService by lazy {
        PhotosService(photosRepository)
    }

    override val documentsRepository: DocumentsRepository by lazy {
        DocumentsRepositoryImpl(wordpressApi)
    }
    override val documentsService: DocumentsService by lazy {
        DocumentsService(documentsRepository)
    }

    override val leitungsteamRepository: LeitungsteamRepository by lazy {
        LeitungsteamRepositoryImpl(wordpressApi)
    }
    override val leitungsteamService: LeitungsteamService by lazy {
        LeitungsteamService(leitungsteamRepository)
    }

    override val naechsteAktivitaetRepository: NaechsteAktivitaetRepository by lazy {
        NaechsteAktivitaetRepositoryImpl(wordpressApi)
    }
    override val naechsteAktivitaetService: NaechsteAktivitaetService by lazy {
        NaechsteAktivitaetService(naechsteAktivitaetRepository, firestoreRepository, selectedStufenRepository)
    }
}