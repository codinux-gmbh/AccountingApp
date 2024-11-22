package net.codinux.accounting.domain.mail.dataaccess

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import net.codinux.accounting.ui.PlatformDependencies
import net.codinux.accounting.ui.config.DI
import net.codinux.invoicing.mail.MailWithInvoice
import java.io.File

class MailRepository(
    private val jsonMapper: ObjectMapper = DI.jsonMapper,
    storageDirectory: File = PlatformDependencies.storageDir
) {

    private val mailsFile = File(storageDirectory, "mails.json")

    private var storedMails: List<MailWithInvoice> = emptyList()


    fun loadMails(): List<MailWithInvoice> =
        if (mailsFile.exists()) {
            jsonMapper.readValue<List<MailWithInvoice>>(mailsFile).also {
                storedMails = it
            }
        } else { // mails have not been persisted yet
            emptyList()
        }

    fun saveMails(mails: Collection<MailWithInvoice>) {
        this.storedMails += mails

        jsonMapper.writeValue(mailsFile, storedMails)
    }

}