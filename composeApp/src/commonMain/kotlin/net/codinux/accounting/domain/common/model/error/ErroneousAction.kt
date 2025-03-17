package net.codinux.accounting.domain.common.model.error

enum class ErroneousAction {
    CreateInvoice,
    ShowEInvoiceInExternalViewer,
    ReadEInvoice,

    AddEmailAccount,
    FetchEmails,
    ListenForNewEmails,

    LoadFromDatabase,
    SaveToDatabase
}