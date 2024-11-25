package net.codinux.accounting.domain.mail.service

import net.codinux.accounting.domain.mail.dataaccess.MailRepository
import net.codinux.accounting.domain.mail.model.MailAccountConfiguration
import net.codinux.accounting.ui.config.DI
import net.codinux.accounting.ui.state.UiState
import net.codinux.invoicing.mail.MailAccount
import net.codinux.invoicing.mail.MailReader
import net.codinux.invoicing.mail.MailWithInvoice
import net.codinux.log.logger

class MailService(
    private val mailReader: MailReader,
    private val repository: MailRepository
) {

    private val log by logger()


    suspend fun init(uiState: UiState) {
        try {
            uiState.mails.value = loadPersistedMails()

            uiState.mailAccounts.value = loadPersistedMailAccounts()
        } catch (e: Throwable) {
            log.error(e) { "Could not initialize persisted mail data" }

            // TODO: show error to user
        }
    }


    private suspend fun loadPersistedMails() = repository.loadMails()

    private suspend fun persistMails(mails: Collection<MailWithInvoice>) {
        try {
            DI.uiState.mails.value = repository.saveMails(mails)
        } catch (e: Throwable) {
            log.error(e) { "Could not persist mails" }

            // TODO: show error to user
        }
    }


    private suspend fun retrieveMails(account: MailAccount): List<MailWithInvoice> = try {
        mailReader.listAllMessagesWithEInvoice(account, true)
    } catch (e: Throwable) {
        log.error(e) { "Could not retrieve mails" }

        // TODO: show error to user

        emptyList()
    }


    private suspend fun loadPersistedMailAccounts() = repository.loadMailAccounts()

    suspend fun addMailAccount(account: MailAccountConfiguration) {
        try {
            DI.uiState.mailAccounts.value = repository.saveMailAccount(account)

            account.receiveEmailConfiguration?.let {
                val newMails = retrieveMails(it)

                persistMails(newMails)
            }
        } catch (e: Throwable) {
            log.error(e) { "Could not add new mail account for $account" }

            // TODO: show error to user
        }
    }

}