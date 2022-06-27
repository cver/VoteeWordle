package com.sondt.voteewordle

import android.app.Application
import androidx.room.Room
import com.google.android.material.color.DynamicColors
import com.sondt.voteewordle.data.remote.ApiService
import com.sondt.voteewordle.data.remote.WordDictService
import com.sondt.voteewordle.data.remote.WordleService
import com.sondt.voteewordle.data.repository.WordDictRepositoryImpl
import com.sondt.voteewordle.data.repository.WordleRepositoryImpl
import com.sondt.voteewordle.domain.repository.WordDictRepository
import com.sondt.voteewordle.domain.repository.WordleRepository
import com.sondt.voteewordle.domain.usecase.*
import com.sondt.voteewordle.ui.main.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

class VoteeWordleApplication: Application(), KoinComponent {
    private val databaseModule = module {
        single(named("internalFileDir")) {
            androidContext().filesDir
        }
        single {
            Room.databaseBuilder(androidContext().applicationContext, AppRoomDatabase::class.java, "VoteeWordleApp").build()
        }
    }

    private val databaseTableModule = module {
        single {
            get<AppRoomDatabase>().wordDao()
        }
    }

    private val dataServiceModule = module {
        single {
            ApiService.buildService(
                url = "https://v1.wordle.k2bd.dev/",
                serviceType = WordleService::class.java,
            )
        }
        single {
            ApiService.buildService(
                url = "https://raw.githubusercontent.com/",
                serviceType = WordDictService::class.java,
            )
        }
    }

    private val repositoryModule = module {
        single<WordleRepository> {
            WordleRepositoryImpl(get())
        }
        single<WordDictRepository> {
            WordDictRepositoryImpl(get(), get(), get(named("internalFileDir")))
        }
    }

    private val useCaseModule = module {
        factory {
            SyncWordDictionaryUseCase(get())
        }
        factory {
            CountWordDictionaryUseCase(get())
        }
        factory {
            GuessRandomFirstWordUseCase(get(), get())
        }
        factory {
            GuessRandomWordUseCase(get(), get())
        }
    }

    private val viewModelModule = module {
        viewModel {
            MainViewModel(get(), get(), get(), get())
        }
    }

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        startKoin {
            androidLogger()
            androidContext(this@VoteeWordleApplication)
            modules(listOf(
                databaseModule,
                databaseTableModule,
                dataServiceModule,
                repositoryModule,
                useCaseModule,
                viewModelModule,
            ))
        }
    }
}
