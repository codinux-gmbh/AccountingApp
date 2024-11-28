package net.codinux.accounting.domain.mail.service

import net.codinux.accounting.domain.common.model.error.ErroneousAction
import net.codinux.accounting.domain.mail.dataaccess.MailRepository
import net.codinux.accounting.domain.mail.model.MailAccountConfiguration
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.config.DI
import net.codinux.accounting.ui.state.UiState
import net.codinux.invoicing.email.EmailsFetcher
import net.codinux.invoicing.email.FetchEmailsOptions
import net.codinux.invoicing.email.model.Email
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

    private suspend fun persistEmails(mails: Collection<Email>) {
        try {
            uiState.mails.value = repository.saveMails(mails)
        } catch (e: Throwable) {
            log.error(e) { "Could not persist emails" }

            uiState.errorOccurred(ErroneousAction.SaveToDatabase, Res.string.error_message_could_not_persist_emails, e)
        }
    }


    private suspend fun fetchAndPersistEmails(account: EmailAccount): List<Email> = try {
        val result = emailsFetcher.fetchAllEmails(account, FetchEmailsOptions(downloadMessageBody = true))

        if (result.overallError != null) {
            uiState.errorOccurred(ErroneousAction.FetchEmails, Res.string.error_message_could_not_fetch_emails, result.overallError)
        }
        result.messageSpecificErrors.forEach { error ->
            uiState.errorOccurred(ErroneousAction.FetchEmails, Res.string.error_message_could_not_fetch_emails, error.error)
        }

        val newEmails = result.emails

        if (newEmails.isNotEmpty()) {
            persistEmails(newEmails)
        }

        newEmails
    } catch (e: Throwable) {
        log.error(e) { "Could not fetch emails of account $account" }

        uiState.errorOccurred(ErroneousAction.FetchEmails, Res.string.error_message_could_not_fetch_emails, e)

        emptyList()
    }


    // errors handled by init()
    private suspend fun loadPersistedMailAccounts() = repository.loadMailAccounts()

    suspend fun addMailAccount(account: MailAccountConfiguration): Boolean {
        try {
            DI.uiState.mailAccounts.value = repository.saveMailAccount(account)

            account.receiveEmailConfiguration?.let {
                fetchAndPersistEmails(it)
            }

            return true
        } catch (e: Throwable) {
            log.error(e) { "Could not add new email account for $account" }

            uiState.errorOccurred(ErroneousAction.SaveToDatabase, Res.string.error_message_could_not_persist_emails, e)
        }

        return false
    }

}