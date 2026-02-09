package io.nekohasekai.sfa.di

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class ApplicationScope

@Qualifier
@Retention(RUNTIME)
annotation class IoDispatcher

@Qualifier
@Retention(RUNTIME)
annotation class DefaultDispatcher

@Qualifier
@Retention(RUNTIME)
annotation class MainDispatcher
