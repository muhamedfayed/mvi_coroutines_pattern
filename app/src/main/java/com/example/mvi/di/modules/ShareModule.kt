package com.example.mvi.di.modules
import com.example.mvi.presentation.share.ShareViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val shareModule = module {
    viewModel { ShareViewModel()}
}
