package net.codinux.accounting.domain.mail.dataaccess

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import net.codinux.accounting.domain.mail.model.MailAccountConfiguration
import net.codinux.accounting.ui.PlatformDependencies
import net.codinux.accounting.ui.config.DI
import net.codinux.invoicing.email.model.Email
import net.codinux.log.logger
import java.io.File

class MailRepository(
    private val jsonMapper: ObjectMapper = DI.jsonMapper,
    storageDirectory: File = PlatformDependencies.storageDir
) {

    private val mailsFile = File(storageDirectory, "mails.json")

    private var storedMails: MutableList<Email> = mutableListOf()

    private val mailAccountsFile = File(storageDirectory, "mailAccounts.json")

    private var storedMailAccounts: MutableList<MailAccountConfiguration> = mutableListOf()

    private val log by logger()


    suspend fun loadMails(): List<Email> =
        if (mailsFile.exists()) {
            try {
                jsonMapper.readValue<MutableList<Email>>(mailsFile).also {
                    storedMails = it

                    log.info { "Deserialized ${it.size} mails" }
                }
            } catch (e: Throwable) {
                log.error(e) { "Could not deserialize mails" }
                emptyList()
            }
        } else { // mails have not been persisted yet
            emptyList()
        }

    suspend fun saveMails(mails: Collection<Email>): List<Email> {
        this.storedMails += mails

        storedMails.sortByDescending { it.received }

        jsonMapper.writeValue(mailsFile, storedMails)

        return storedMails
    }


    suspend fun loadMailAccounts(): List<MailAccountConfiguration> =
        if (mailAccountsFile.exists()) {
            try {
                jsonMapper.readValue<MutableList<MailAccountConfiguration>>(mailAccountsFile).also {
                    storedMailAccounts = it
                }
            } catch (e: Throwable) {
                log.error(e) { "Could not deserialize mail accounts" }
                emptyList()
            }
        } else { // mail accounts have not been persisted yet
            emptyList()
        }

    suspend fun saveMailAccount(account: MailAccountConfiguration): List<MailAccountConfiguration> {
        this.storedMailAccounts += account

        jsonMapper.writeValue(mailAccountsFile, storedMailAccounts)

        return storedMailAccounts
    }

}