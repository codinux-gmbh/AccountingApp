package net.codinux.accounting.domain.mail.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
            uiState.mails.value = loadPersistedMails()

            uiState.mailAccounts.value = loadPersistedMailAccounts()
        } catch (e: Throwable) {
            log.error(e) { "Could not initialize persisted email data" }

            uiState.errorOccurred(ErroneousAction.LoadFromDatabase, Res.string.error_message_could_not_load_emails, e)
        }
    }


    // errors handled by init()
    private suspend fun loadPersistedMails() = repository.loadMails()

    private suspend fun persistEmails(account: MailAccountConfiguration, emails: Collection<net.codinux.invoicing.email.model.Email>) {
        try {
            uiState.mails.value = repository.saveMails(account, emails)
        } catch (e: Throwable) {
            log.error(e) { "Could not persist emails" }

            uiState.errorOccurred(ErroneousAction.SaveToDatabase, Res.string.error_message_could_not_persist_emails, e)
        }
    }


    private suspend fun fetchAndPersistEmails(configuration: MailAccountConfiguration, fetchAccount: EmailAccount) = try {
        val result = emailsFetcher.fetchAllEmails(fetchAccount, FetchEmailsOptions(downloadMessageBody = true, downloadOnlyPlainTextOrHtmlMessageBody = true))

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
            DI.uiState.mailAccounts.value = repository.saveMailAccount(account)

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