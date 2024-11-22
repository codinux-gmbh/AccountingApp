package net.codinux.accounting.domain.mail.service

import net.codinux.accounting.domain.mail.dataaccess.MailRepository
import net.codinux.invoicing.mail.MailReader
import net.codinux.invoicing.mail.MailWithInvoice

class MailService(
    private val repository: MailRepository,
    private val mailReader: MailReader = MailReader()
) {

    fun loadPersistedMails() = repository.loadMails()

    fun persistMails(mails: Collection<MailWithInvoice>) = repository.saveMails(mails)

}