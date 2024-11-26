package net.codinux.accounting.domain.mail.model

import net.codinux.invoicing.email.model.EmailAccount

data class MailAccountConfiguration(
    var receiveEmailConfiguration: EmailAccount? = null,
    var sendEmailConfiguration: EmailAccount? = null
)