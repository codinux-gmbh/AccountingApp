package net.codinux.accounting.domain.mail.dataaccess

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import net.codinux.accounting.domain.mail.model.Email
import net.codinux.accounting.domain.mail.model.MailAccountConfiguration
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList

class MailRepository(
    private val jsonMapper: ObjectMapper,
    dataDirectory: File
) {

    private val mailsFile = File(dataDirectory, "mails.json")

    private var storedMails: MutableList<Email> = CopyOnWriteArrayList()

    private val mailAccountsFile = File(dataDirectory, "mailAccounts.json")

    private var storedMailAccounts: MutableList<MailAccountConfiguration> = CopyOnWriteArrayList()


    /**
     * Does not handle errors, [net.codinux.accounting.domain.mail.service.JvmMailService.init] does this for us
     */
    suspend fun loadMails(): List<Email> =
        if (mailsFile.exists()) {
            jsonMapper.readValue<MutableList<Email>>(mailsFile).also {
                storedMails = CopyOnWriteArrayList(it)
            }
        } else { // mails have not been persisted yet
            emptyList()
        }

    suspend fun saveMails(account: MailAccountConfiguration, emails: Collection<net.codinux.invoicing.email.model.Email>): List<Email> {
        val mapped = emails.mapIndexed { index, it -> Email((storedMails.size + index).toLong(), account.id!!, it.messageId, it.sender, it.subject, it.date, it.plainTextOrHtmlBody!!, it.contentLanguage, it.isEncrypted, it.attachments) }

        this.storedMails += mapped

        storedMails.sortByDescending { it.date }

        jsonMapper.writeValue(mailsFile, storedMails)

        return storedMails
    }


    suspend fun loadMailAccounts(): List<MailAccountConfiguration> =
        if (mailAccountsFile.exists()) {
            jsonMapper.readValue<MutableList<MailAccountConfiguration>>(mailAccountsFile).also {
                storedMailAccounts = CopyOnWriteArrayList(it)
            }
        } else { // mail accounts have not been persisted yet
            emptyList()
        }

    suspend fun saveMailAccount(account: MailAccountConfiguration): List<MailAccountConfiguration> {
        account.id = this.storedMailAccounts.size.toLong()

        this.storedMailAccounts += account

        jsonMapper.writeValue(mailAccountsFile, storedMailAccounts)

        return storedMailAccounts
    }

}