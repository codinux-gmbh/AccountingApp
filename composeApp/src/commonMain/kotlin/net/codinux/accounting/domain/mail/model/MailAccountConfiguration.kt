package net.codinux.accounting.domain.mail.model

import net.codinux.invoicing.mail.MailAccount

data class MailAccountConfiguration(
    var receiveEmailConfiguration: MailAccount? = null,
    var sendEmailConfiguration: MailAccount? = null
)