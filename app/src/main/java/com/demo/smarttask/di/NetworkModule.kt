package com.demo.smarttask.di

import com.demo.smarttask.data.remote.TodoApiService
import com.demo.smarttask.data.remote.TodoRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://dummyjson.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideTodoApiService(
        retrofit: Retrofit
    ): TodoApiService {
        return retrofit.create(TodoApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideTodoRemoteDataSource(
        apiService: TodoApiService
    ): TodoRemoteDataSource {
        return TodoRemoteDataSource(apiService)
    }
}