package net.codinux.accounting.domain.common.model.error

enum class ErroneousAction {
    ShowSelectedEInvoice,
    ReadEInvoice,

    CreateInvoice,
    ShowEInvoiceInExternalViewer,

    AddEmailAccount,
    FetchEmails,
    ListenForNewEmails,

    LoadFromDatabase,
    SaveToDatabase
}