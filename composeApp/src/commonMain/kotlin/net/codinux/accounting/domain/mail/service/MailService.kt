package net.codinux.accounting.domain.mail.service

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.toList
import net.codinux.accounting.domain.common.model.error.ErroneousAction
import net.codinux.accounting.domain.mail.dataaccess.MailRepository
import net.codinux.accounting.domain.mail.model.MailAccountConfiguration
import net.codinux.accounting.resources.*
import net.codinux.accounting.ui.config.DI
import net.codinux.accounting.ui.state.UiState
import net.codinux.invoicing.email.*
import net.codinux.invoicing.email.model.*
import net.codinux.log.logger
import java.util.concurrent.atomic.AtomicBoolean

class MailService(
    private val uiState: UiState,
    private val emailsFetcher: EmailsFetcher,
    private val repository: MailRepository,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {

    private val log by logger()


    suspend fun init() {
        try {
            uiState.emails.mailAccounts.emit(loadPersistedMailAccounts().toList())

            uiState.emails.mails.emit(loadPersistedMails().toList()) // make a copy. otherwise the same instance of MailRepository.allMails would be passed to uiState.mails which therefore cannot detect changes
        } catch (e: Throwable) {
            log.error(e) { "Could not initialize persisted email data" }

            uiState.errorOccurred(ErroneousAction.LoadFromDatabase, Res.string.error_message_could_not_load_emails, e)
        }

        uiState.emails.mailAccounts.value.forEach { account ->
            val lastRetrievedMessageId = uiState.emails.mails.value.filter { it.emailAccountId == account.id }.maxOfOrNull { it.messageId }

            coroutineScope.launch(Dispatchers.IO) {
                fetchAndListenToNewMails(account, lastRetrievedMessageId)
            }
        }
    }


    // errors handled by init()
    private suspend fun loadPersistedMails() = repository.loadMails()

    private suspend fun persistEmails(account: MailAccountConfiguration, emails: Collection<Email>) {
        try {
            if (emails.isNotEmpty()) {
                val allMails = repository.saveMails(account, emails)
                uiState.emails.mails.emit(allMails.toList()) // make a copy. otherwise the same instance of MailRepository.allMails would be passed to uiState.mails which therefore cannot detect changes
            }
        } catch (e: Throwable) {
            log.error(e) { "Could not persist emails of account ${account.receiveEmailConfiguration}" }

            uiState.errorOccurred(ErroneousAction.SaveToDatabase, Res.string.error_message_could_not_persist_emails, e)
        }
    }


    private suspend fun fetchAndListenToNewMails(configuration: MailAccountConfiguration, lastRetrievedMessageId: Long? = null) {
        configuration.receiveEmailConfiguration?.let { account ->
            try {
                fetchAndPersistEmails(configuration, account, lastRetrievedMessageId)
            } catch (e: Throwable) {
                log.error(e) { "Fetching new emails of account $account failed" }
            }

            try {
                emailsFetcher.listenForNewEmails(account, ListenForNewMailsOptions(onError = { handleFetchEmailError(account, it) }) { newEmail ->
                    coroutineScope.launch(Dispatchers.IO) {
                        persistEmails(configuration, listOf(newEmail))
                    }
                })
            } catch (e: Throwable) {
                log.error(e) { "Listening to new emails of account $account failed" }
            }
        }
    }

    private fun handleFetchEmailError(account: EmailAccount, error: FetchEmailError) {
        if (error.type == FetchEmailErrorType.ListenForNewEmails) {
            uiState.errorOccurred(ErroneousAction.ListenForNewEmails, Res.string.error_message_could_not_listen_for_new_emails, error.error, account.toString())
        }
    }

    private suspend fun fetchAndPersistEmails(configuration: MailAccountConfiguration, fetchAccount: EmailAccount, lastRetrievedMessageId: Long? = null) = try {
        val channel = Channel<Email>(Channel.UNLIMITED)
        val isFetchingMails = AtomicBoolean(true)

        coroutineScope.launch(Dispatchers.IO) {
            persistEmailsBatched(configuration, channel, isFetchingMails)
        }

        val result = emailsFetcher.fetchAllEmails(fetchAccount, FetchEmailsOptions(lastRetrievedMessageId) { newEmail ->
            channel.trySend(newEmail)
        })

        isFetchingMails.set(false)


        if (result.overallError != null) {
            uiState.errorOccurred(ErroneousAction.FetchEmails, Res.string.error_message_could_not_fetch_emails, result.overallError)
        }
        result.messageSpecificErrors.forEach { error ->
            // these messages are too detailed for most users
            // uiState.errorOccurred(ErroneousAction.FetchEmails, Res.string.error_message_could_not_fetch_emails, error.error)
        }
    } catch (e: Throwable) {
        log.error(e) { "Could not fetch emails of account $fetchAccount" }

        uiState.errorOccurred(ErroneousAction.FetchEmails, Res.string.error_message_could_not_fetch_emails, e)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun persistEmailsBatched(configuration: MailAccountConfiguration, channel: Channel<Email>, isFetchingMails: AtomicBoolean) {
        while (isFetchingMails.get() || channel.isEmpty == false) {
            delay(1_000) // to reduce disk I/O batch received emails and persist them only once a second

            val newEmails = buildList {
                var result = channel.tryReceive()
                while (result.isSuccess) {
                    result.getOrNull()?.let { add(it) }

                    result = channel.tryReceive()
                }
            }

            persistEmails(configuration, newEmails)
        }

        val remainingEmails = channel.toList()
        persistEmails(configuration, remainingEmails)
    }


    // errors handled by init()
    private suspend fun loadPersistedMailAccounts() = repository.loadMailAccounts()

    suspend fun addMailAccount(account: MailAccountConfiguration, scope: CoroutineScope): Boolean {
        try {
            val checkCredentialsResult = emailsFetcher.checkCredentials(account.receiveEmailConfiguration ?: account.sendEmailConfiguration!!)
            if (checkCredentialsResult != CheckCredentialsResult.Ok) {
                // TODO: translate checkCredentialsResult
                uiState.errorOccurred(ErroneousAction.FetchEmails, Res.string.error_message_checking_email_account_credentials_failed, null, listOf(checkCredentialsResult))

                return false
            }

            DI.uiState.emails.mailAccounts.value = repository.saveMailAccount(account)

            account.receiveEmailConfiguration?.let {
                scope.launch(Dispatchers.IO) {
                    fetchAndPersistEmails(account, it)
                }
            }

            return true
        } catch (e: Throwable) {
            log.error(e) { "Could not add new email account for $account" }

            uiState.errorOccurred(ErroneousAction.SaveToDatabase, Res.string.error_message_could_not_persist_emails, e)
        }

        return false
    }

}