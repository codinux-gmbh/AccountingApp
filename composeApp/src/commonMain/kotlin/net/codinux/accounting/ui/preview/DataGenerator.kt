package net.codinux.accounting.ui.preview

import net.codinux.accounting.domain.mail.model.Email
import net.codinux.invoicing.email.model.ContentDisposition
import net.codinux.invoicing.email.model.EmailAddress
import net.codinux.invoicing.email.model.EmailAttachment
import net.codinux.invoicing.model.*
import net.codinux.invoicing.model.codes.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DataGenerator {

    const val InvoiceNumber = "12345"
    val InvoicingDate = LocalDate.of(2015, 10, 21)
    val DueDate = LocalDate.of(2016, 6, 15)

    const val SupplierName = "Hochwürdiger Leistungserbringer"
    const val SupplierAddress = "Fun Street 1"
    val SupplierAdditionalAddressLine: String? = null
    const val SupplierPostalCode = "12345"
    const val SupplierCity = "Glückstadt"
    val SupplierCountry = Country.Germany
    const val SupplierVatId = "DE123456789"
    const val SupplierEmail = "working-class-hero@rock.me"
    const val SupplierPhone = "+4917012345678"
    val SupplierBankDetails = BankDetails("DE00123456780987654321", "ABZODEFFXXX", "Manuela Musterfrau")

    const val CustomerName = "Untertänigster Leistungsempfänger"
    const val CustomerAddress = "Party Street 1"
    val CustomerAdditionalAddressLine: String? = null
    const val CustomerPostalCode = SupplierPostalCode
    const val CustomerCity = SupplierCity
    val CustomerCountry = SupplierCountry
    const val CustomerVatId = "DE987654321"
    const val CustomerEmail = "exploiter@your.boss"
    const val CustomerPhone = "+491234567890"
    val CustomerBankDetails: BankDetails? = null

    const val ItemName = "Erbrachte Dienstleistungen"
    val ItemUnit = UnitOfMeasure.HUR
    val ItemQuantity = BigDecimal(1)
    val ItemUnitPrice = BigDecimal(99)
    val ItemVatRate = BigDecimal(19)
    val ItemDescription: String? = null


    fun createInvoice(
        invoiceNumber: String = InvoiceNumber,
        invoicingDate: LocalDate = InvoicingDate,
        supplier: Party = createParty(SupplierName, SupplierAddress, SupplierAdditionalAddressLine, SupplierPostalCode, SupplierCity, SupplierCountry, SupplierVatId, SupplierEmail, SupplierPhone,
            bankDetails = SupplierBankDetails),
        customer: Party = createParty(CustomerName, CustomerAddress, CustomerAdditionalAddressLine, CustomerPostalCode, CustomerCity, CustomerCountry, CustomerVatId, CustomerEmail, CustomerPhone,
            bankDetails = CustomerBankDetails),
        items: List<InvoiceItem> = listOf(createItem()),
        currency: Currency = Currency.Euro,
        dueDate: LocalDate? = DueDate,
        paymentDescription: String? = dueDate?.let { "Zahlbar ohne Abzug bis ${DateTimeFormatter.ofPattern("dd.MM.yyyy").format(dueDate)}" },
        customerReferenceNumber: String? = null
    ) = Invoice(InvoiceDetails(invoiceNumber, invoicingDate, currency, dueDate, paymentDescription), supplier, customer, items, customerReferenceNumber)

    fun createParty(
        name: String,
        address: String = SupplierAddress,
        additionalAddressLine: String? = SupplierAdditionalAddressLine,
        postalCode: String = SupplierPostalCode,
        city: String = SupplierCity,
        country: Country = SupplierCountry,
        vatId: String? = SupplierVatId,
        email: String? = SupplierEmail,
        phone: String? = SupplierPhone,
        fax: String? = null,
        contactName: String? = null,
        bankDetails: BankDetails? = null
    ) = Party(name, address, additionalAddressLine, postalCode, city, country, vatId, email, phone, fax, contactName, bankDetails)

    fun createItem(
        name: String = ItemName,
        quantity: BigDecimal = ItemQuantity,
        unit: UnitOfMeasure = ItemUnit,
        unitPrice: BigDecimal = ItemUnitPrice,
        vatRate: BigDecimal = ItemVatRate,
        description: String? = ItemDescription,
    ) = InvoiceItem(name, quantity, unit, unitPrice, vatRate, description)


    fun createMail(invoice: Invoice, messageId: Long = 1) =
        Email(messageId, messageId, messageId, invoice.supplier.email?.let { EmailAddress(it) }, "Invoice No. ${invoice.details.invoiceNumber}", invoice.details.invoiceDate.atStartOfDay(ZoneId.systemDefault()).toInstant(),
            "Sehr geehrter Herr Sowieso,\nanbei unsere völlig überzogene Rechnung für unsere nutzlosen Dienstleistung mit Bitte um Überweisung innerhalb 24 Minuten.\nGezeichnet,\nHerr Geier",
            attachments = listOf(EmailAttachment("invoice.pdf", "pdf", null, ContentDisposition.Attachment, "application/pdf", null, invoice, null))
        )

}