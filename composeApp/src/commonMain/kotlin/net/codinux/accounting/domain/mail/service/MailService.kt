package net.codinux.accounting.domain.mail.service

import kotlinx.coroutines.*
import net.codinux.accounting.domain.common.model.error.ErroneousAction
import net.codinux.accounting.domain.mail.dataaccess.MailRepository
import net.codinux.accounting.domain.mail.model.MailAccountConfiguration
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.config.DI
import net.codinux.accounting.ui.state.UiState
import net.codinux.invoicing.email.*
import net.codinux.invoicing.email.model.EmailAccount
import net.codinux.log.logger

class MailService(
    private val uiState: UiState,
    private val emailsFetcher: EmailsFetcher,
    private val repository: MailRepository
) {

    private val log by logger()


    suspend fun init() {
        try {
            val allEmails = loadPersistedMails()
            uiState.emails.mails.emit(allEmails.toList()) // make a copy. otherwise the same instance of MailRepository.allMails would be passed to uiState.mails which therefore cannot detect changes

            uiState.emails.mailAccounts.emit(loadPersistedMailAccounts())

            uiState.emails.mailAccounts.value.forEach { account ->
                withContext(Dispatchers.IO) {
                    val lastRetrievedMessageId = allEmails.filter { it.emailAccountId == account.id }.maxOfOrNull { it.messageId }

                    launch { fetchAndListenToNewMails(account, lastRetrievedMessageId, this) }
                }
            }
        } catch (e: Throwable) {
            log.error(e) { "Could not initialize persisted email data" }

            uiState.errorOccurred(ErroneousAction.LoadFromDatabase, Res.string.error_message_could_not_load_emails, e)
        }
    }


    // errors handled by init()
    private suspend fun loadPersistedMails() = repository.loadMails()

    private suspend fun persistEmails(account: MailAccountConfiguration, emails: Collection<net.codinux.invoicing.email.model.Email>) {
        try {
            val allMails = repository.saveMails(account, emails)
            uiState.emails.mails.emit(allMails.toList()) // make a copy. otherwise the same instance of MailRepository.allMails would be passed to uiState.mails which therefore cannot detect changes
        } catch (e: Throwable) {
            log.error(e) { "Could not persist emails of account ${account.receiveEmailConfiguration}" }

            uiState.errorOccurred(ErroneousAction.SaveToDatabase, Res.string.error_message_could_not_persist_emails, e)
        }
    }


    private suspend fun fetchAndListenToNewMails(configuration: MailAccountConfiguration, lastRetrievedMessageId: Long? = null, scope: CoroutineScope) {
        configuration.receiveEmailConfiguration?.let { account ->
             fetchAndPersistEmails(configuration, account, lastRetrievedMessageId)

            try {
                emailsFetcher.listenForNewEmails(account, ListenForNewMailsOptions(downloadMessageBody = true, downloadOnlyPlainTextOrHtmlMessageBody = true, onError = { handleFetchEmailError(it) }) { newEmail ->
                    scope.launch {
                        persistEmails(configuration, listOf(newEmail))
                    }
                })
            } catch (e: Throwable) {
                log.error(e) { "Listening to new emails failed" }
            }
        }
    }

    private fun handleFetchEmailError(error: FetchEmailError) {

    }

    private suspend fun fetchAndPersistEmails(configuration: MailAccountConfiguration, fetchAccount: EmailAccount, lastRetrievedMessageId: Long? = null) = try {
        val result = emailsFetcher.fetchAllEmails(fetchAccount, FetchEmailsOptions(lastRetrievedMessageId, downloadMessageBody = true, downloadOnlyPlainTextOrHtmlMessageBody = true))

        if (result.overallError != null) {
            uiState.errorOccurred(ErroneousAction.FetchEmails, Res.string.error_message_could_not_fetch_emails, result.overallError)
        }
        result.messageSpecificErrors.forEach { error ->
            uiState.errorOccurred(ErroneousAction.FetchEmails, Res.string.error_message_could_not_fetch_emails, error.error)
        }

        val newEmails = result.emails

        if (newEmails.isNotEmpty()) {
            persistEmails(configuration, newEmails)
        }
        Unit
    } catch (e: Throwable) {
        log.error(e) { "Could not fetch emails of account $fetchAccount" }

        uiState.errorOccurred(ErroneousAction.FetchEmails, Res.string.error_message_could_not_fetch_emails, e)
    }


    // errors handled by init()
    private suspend fun loadPersistedMailAccounts() = repository.loadMailAccounts()

    suspend fun addMailAccount(account: MailAccountConfiguration): Boolean {
        try {
            DI.uiState.emails.mailAccounts.value = repository.saveMailAccount(account)

            account.receiveEmailConfiguration?.let {
                fetchAndPersistEmails(account, it)
            }

            return true
        } catch (e: Throwable) {
            log.error(e) { "Could not add new email account for $account" }

            uiState.errorOccurred(ErroneousAction.SaveToDatabase, Res.string.error_message_could_not_persist_emails, e)
        }

        return false
    }

}