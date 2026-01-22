package com.example.kriptogan.data.database.dao

import androidx.room.*
import com.example.kriptogan.data.model.EmailConfig
import kotlinx.coroutines.flow.Flow

@Dao
interface EmailConfigDao {
    @Query("SELECT * FROM email_config WHERE id = 1 LIMIT 1")
    suspend fun getEmailConfig(): EmailConfig?

    @Query("SELECT * FROM email_config WHERE id = 1 LIMIT 1")
    fun getEmailConfigFlow(): Flow<EmailConfig?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmailConfig(config: EmailConfig)

    @Update
    suspend fun updateEmailConfig(config: EmailConfig)

    @Query("DELETE FROM email_config")
    suspend fun deleteEmailConfig()
}
