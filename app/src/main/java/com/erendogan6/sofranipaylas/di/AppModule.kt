package com.erendogan6.sofranipaylas.di

import android.content.Context
import com.erendogan6.sofranipaylas.repository.Repository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideUserRepository(firebaseAuth: FirebaseAuth, firestore: FirebaseFirestore, storage: FirebaseStorage,
                              @ApplicationContext
                              context: Context): Repository {
        return Repository(firebaseAuth, firestore, storage, context)
    }
}
