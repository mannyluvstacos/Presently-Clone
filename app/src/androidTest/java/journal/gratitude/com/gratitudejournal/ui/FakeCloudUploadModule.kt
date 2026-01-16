package journal.gratitude.com.gratitudejournal.ui

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import journal.gratitude.com.gratitudejournal.di.CloudUploadModule
import journal.gratitude.com.gratitudejournal.fakes.FakeLocalBackupProvider
import journal.gratitude.com.gratitudejournal.fakes.FakeUploader
import journal.gratitude.com.gratitudejournal.util.backups.LocalBackupProvider
import journal.gratitude.com.gratitudejournal.util.backups.Uploader
import javax.inject.Singleton

/**
 * LocalBackupProvider binding to use in tests.
 *
 * Hilt will inject a [FakeLocalBackupProvider] instead of a real [LocalBackupProvider].
 */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [CloudUploadModule::class]
)
abstract class FakeCloudUploadModule {
    
    companion object {
        @Provides
        fun provideLocalBackupProvider(@ApplicationContext context: Context): LocalBackupProvider {
            return FakeLocalBackupProvider(context)
        }
    }

    @Singleton
    @Binds
    abstract fun provideUploader(uploader: FakeUploader): Uploader
}