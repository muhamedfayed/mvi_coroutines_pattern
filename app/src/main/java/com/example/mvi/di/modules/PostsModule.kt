package com.example.mvi.di.modules
import com.example.mvi.data.posts.api.PostsApi
import com.example.mvi.data.posts.repository.PostsRepositoryImpl
import com.example.mvi.domain.posts.PostsRepository
import com.example.mvi.domain.posts.usercase.PostsUseCase
import com.example.mvi.presentation.home.HomeViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit

val postsModule = module {
    single { providePostsApi(get()) }
    single<PostsRepository> {
        PostsRepositoryImpl(get())
    } bind PostsRepository::class

    single { providePostsUseCase(get()) }

    viewModel { HomeViewModel(get()) }
}

fun providePostsApi(retrofit: Retrofit): PostsApi {
    return retrofit.create(PostsApi::class.java)
}
fun providePostsUseCase(postsRepository: PostsRepository): PostsUseCase {
    return PostsUseCase(postsRepository)
}

