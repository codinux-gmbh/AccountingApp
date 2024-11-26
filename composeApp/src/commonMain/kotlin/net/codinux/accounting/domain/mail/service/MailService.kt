package net.codinux.accounting.domain.mail.service

import net.codinux.accounting.domain.common.model.error.ErroneousAction
import net.codinux.accounting.domain.mail.dataaccess.MailRepository
import net.codinux.accounting.domain.mail.model.MailAccountConfiguration
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.config.DI
import net.codinux.accounting.ui.state.UiState
import net.codinux.invoicing.mail.MailAccount
import net.codinux.invoicing.mail.MailReader
import net.codinux.invoicing.mail.MailWithInvoice
import net.codinux.log.logger

class MailService(
    private val uiState: UiState,
    private val mailReader: MailReader,
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


    private suspend fun loadPersistedMails() = repository.loadMails()

    private suspend fun persistMails(mails: Collection<MailWithInvoice>) {
        try {
            uiState.mails.value = repository.saveMails(mails)
        } catch (e: Throwable) {
            log.error(e) { "Could not persist emails" }

            uiState.errorOccurred(ErroneousAction.SaveToDatabase, Res.string.error_message_could_not_persist_emails, e)
        }
    }


    private suspend fun fetchEmails(account: MailAccount): List<MailWithInvoice> = try {
        val result = mailReader.listAllMessagesWithEInvoice(account, true)

        if (result.overallError != null) {
            uiState.errorOccurred(ErroneousAction.FetchEmails, Res.string.error_message_could_not_fetch_emails, result.overallError)
        }
        result.messageSpecificErrors.forEach { error ->
            uiState.errorOccurred(ErroneousAction.FetchEmails, Res.string.error_message_could_not_fetch_emails, error.error)
        }

        result.emails
    } catch (e: Throwable) {
        log.error(e) { "Could not fetch emails of account $account" }

        uiState.errorOccurred(ErroneousAction.FetchEmails, Res.string.error_message_could_not_fetch_emails, e)

        emptyList()
    }


    private suspend fun loadPersistedMailAccounts() = repository.loadMailAccounts()

    suspend fun addMailAccount(account: MailAccountConfiguration): Boolean {
        try {
            DI.uiState.mailAccounts.value = repository.saveMailAccount(account)

            account.receiveEmailConfiguration?.let {
                val newMails = fetchEmails(it)

                persistMails(newMails)
            }

            return true
        } catch (e: Throwable) {
            log.error(e) { "Could not add new email account for $account" }

            uiState.errorOccurred(ErroneousAction.SaveToDatabase, Res.string.error_message_could_not_persist_emails, e)
        }

        return false
    }

}