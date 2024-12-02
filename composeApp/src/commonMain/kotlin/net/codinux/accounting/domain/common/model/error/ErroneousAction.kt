package net.codinux.accounting.domain.common.model.error

enum class ErroneousAction {
    CreateInvoice,
    ReadEInvoice,

    AddEmailAccount,
    FetchEmails,
    ListenForNewEmails,

    LoadFromDatabase,
    SaveToDatabase
}