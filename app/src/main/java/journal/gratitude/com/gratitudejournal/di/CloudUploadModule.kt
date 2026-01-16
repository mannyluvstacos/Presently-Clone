package journal.gratitude.com.gratitudejournal.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import journal.gratitude.com.gratitudejournal.util.backups.LocalBackupProvider
import journal.gratitude.com.gratitudejournal.util.backups.RealUploader
import journal.gratitude.com.gratitudejournal.util.backups.Uploader


@Module
@InstallIn(SingletonComponent::class)
object CloudUploadModule {

    @Provides
    fun provideLocalBackupProvider(@ApplicationContext context: Context): LocalBackupProvider {
        return LocalBackupProvider(context)
    }

    @Provides
    fun provideUploader(uploader: RealUploader): Uploader = uploader
}