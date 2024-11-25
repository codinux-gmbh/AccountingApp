package net.codinux.accounting.domain.mail.service

import net.codinux.accounting.domain.mail.dataaccess.MailRepository
import net.codinux.accounting.ui.config.DI
import net.codinux.invoicing.mail.MailAccount
import net.codinux.invoicing.mail.MailReader
import net.codinux.invoicing.mail.MailWithInvoice
import net.codinux.log.logger

class MailService(
    private val mailReader: MailReader,
    private val repository: MailRepository
) {

    private val log by logger()


    suspend fun init() {
        try {
            DI.uiState.mails.value = loadPersistedMails()
        } catch (e: Throwable) {
            log.error(e) { "Could not initialize persisted mail data" }

            // TODO: show error to user
        }
    }


    private suspend fun loadPersistedMails() = repository.loadMails()

    private suspend fun persistMails(mails: Collection<MailWithInvoice>) {
        try {
            repository.saveMails(mails)
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

}