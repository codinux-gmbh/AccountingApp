package net.codinux.accounting.domain.common.model.error

enum class ErroneousAction {
    CreateInvoice,

    AddEmailAccount,
    FetchEmails,

    LoadFromDatabase,
    SaveToDatabase
}