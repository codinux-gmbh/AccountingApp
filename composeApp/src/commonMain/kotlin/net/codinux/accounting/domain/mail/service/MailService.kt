package net.codinux.accounting.domain.mail.service

import net.codinux.accounting.domain.mail.dataaccess.MailRepository
import net.codinux.invoicing.mail.MailAccount
import net.codinux.invoicing.mail.MailReader
import net.codinux.invoicing.mail.MailWithInvoice

class MailService(
    private val mailReader: MailReader,
    private val repository: MailRepository
) {

    fun loadPersistedMails() = repository.loadMails()

    fun persistMails(mails: Collection<MailWithInvoice>) = repository.saveMails(mails)


    fun retrieveMails(account: MailAccount): List<MailWithInvoice> =
        mailReader.listAllMessagesWithEInvoice(account, true)

}