package net.codinux.accounting.domain.mail.service

import kotlinx.coroutines.CoroutineScope
import net.codinux.accounting.domain.mail.model.MailAccountConfiguration

interface MailService {

    suspend fun init()

    fun close()

    suspend fun addMailAccount(account: MailAccountConfiguration, scope: CoroutineScope): Boolean

}