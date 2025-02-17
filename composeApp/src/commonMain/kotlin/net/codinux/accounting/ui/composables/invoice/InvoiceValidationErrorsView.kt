package net.codinux.accounting.ui.composables.invoice

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.codinux.accounting.resources.*
import net.codinux.accounting.resources.Res
import net.codinux.accounting.resources.invoice_errors_data_missing_or_incorrect
import net.codinux.accounting.resources.invoice_errors_violated_business_rules
import net.codinux.accounting.ui.composables.HeaderText
import net.codinux.accounting.ui.composables.forms.HorizontalLabelledValue
import net.codinux.accounting.ui.composables.forms.Section
import net.codinux.accounting.ui.config.Style
import net.codinux.invoicing.model.*
import net.codinux.invoicing.validation.*
import org.jetbrains.compose.resources.stringResource


private val ErrorListItemHorizontalPadding = 8.dp

@Composable
fun InvoiceValidationErrorsView(
    mapInvoiceResult: MapInvoiceResult,
    xmlValidationResult: Result<InvoiceXmlValidationResult>?,
    pdfValidationResult: Result<PdfValidationResult>?
) {

    @Composable
    fun getPdfAVersion(pdfValidationResult: PdfValidationResult): String =
        if (pdfValidationResult.isPdfA == false) stringResource(Res.string.not_a_pdf_a_file)
        else when (pdfValidationResult.pdfAFlavor) {
            PdfAFlavour.WCAG2_1, PdfAFlavour.WCAG2_2 -> pdfValidationResult.pdfAFlavor.name.replace("WCAG2_", "WCAG 2.")
            else -> pdfValidationResult.pdfAFlavor.name.replace("PDFA_", "PDF/A-").replace("PDFUA_", "PDF/UA-").replace("_", "")
        }


    if (mapInvoiceResult.invoiceDataErrors.isNotEmpty() || xmlValidationResult?.value?.resultItems?.isNotEmpty() == true) {
        Section(Res.string.incorrect_invoice_data) {
            if (mapInvoiceResult.invoiceDataErrors.isNotEmpty()) {
                HeaderText(Res.string.invoice_errors_data_missing_or_incorrect, Modifier.fillMaxWidth().padding(top = Style.SectionTopPadding * 2), textAlign = TextAlign.Center, fontSize = 15.sp)

                mapInvoiceResult.invoiceDataErrors.forEach { dataError ->
                    InvoiceDataErrorListItem(dataError)
                }
            }

            xmlValidationResult?.value?.resultItems?.let { resultItems ->
                if (resultItems.isNotEmpty()) {
                    HeaderText(Res.string.invoice_errors_violated_business_rules, Modifier.fillMaxWidth().padding(top = Style.SectionTopPadding * 2), textAlign = TextAlign.Center, fontSize = 15.sp)

                    resultItems.forEach { XmlValidationErrorListItem(it) }
                }
            }
        }
    }

    pdfValidationResult?.value?.let { pdfValidationResult ->
        if (pdfValidationResult.isValid == false) {
            Section(Res.string.incorrect_pdf_file) {
                HorizontalLabelledValue(Res.string.pdf_a_version, getPdfAVersion(pdfValidationResult))

                HeaderText(Res.string.pdf_file_error_not_a_pdf_a3, Modifier.fillMaxWidth().padding(top = Style.SectionTopPadding * 2),
                    textAlign = TextAlign.Center, fontSize = 15.sp)

                if (pdfValidationResult.validationErrors.isNotEmpty()) {
                    HeaderText(Res.string.pdf_file_errors, Modifier.fillMaxWidth().padding(top = Style.SectionTopPadding * 2),
                        textAlign = TextAlign.Center, fontSize = 15.sp)

                    pdfValidationResult.validationErrors.forEach { PdfValidationErrorListItem(it) }
                }
            }
        }
    }
}

@Composable
private fun InvoiceDataErrorListItem(dataError: InvoiceDataError) {
    val fieldName = when (dataError.field) {
        InvoiceField.InvoiceDate -> Res.string.invoice_field_invoice_date
        InvoiceField.InvoiceNumber -> Res.string.invoice_field_invoice_number

        InvoiceField.Currency -> Res.string.invoice_field_currency

        InvoiceField.Supplier -> Res.string.invoice_field_supplier
        InvoiceField.SupplierName -> Res.string.invoice_field_supplier_name
        InvoiceField.SupplierCountry -> Res.string.invoice_field_supplier_country
        InvoiceField.Customer -> Res.string.invoice_field_customer
        InvoiceField.CustomerName -> Res.string.invoice_field_customer_name
        InvoiceField.CustomerCountry -> Res.string.invoice_field_customer_country

        InvoiceField.Items -> Res.string.invoice_field_items
        InvoiceField.ItemName -> Res.string.invoice_field_item_name
        InvoiceField.ItemQuantity -> Res.string.invoice_field_item_quantity
        InvoiceField.ItemUnit -> Res.string.invoice_field_item_unit
        InvoiceField.ItemUnitPrice -> Res.string.invoice_field_item_unit_price

        InvoiceField.TotalAmount -> Res.string.invoice_field_total_amounts
        InvoiceField.LineTotalAmount -> Res.string.invoice_field_line_total_amount
        InvoiceField.TaxBasisTotalAmount -> Res.string.invoice_field_tax_basis_total_amount
        InvoiceField.GrandTotalAmount -> Res.string.invoice_field_grand_total_amount
        InvoiceField.DuePayableAmount -> Res.string.invoice_field_due_payable_amount
    }

    val errorMessage = when (dataError.errorType) {
        InvoiceDataErrorType.ValueNotSet -> Res.string.invoice_data_error_value_not_set
        InvoiceDataErrorType.ValueNotUpperCase -> Res.string.invoice_data_error_value_not_uppercase
        InvoiceDataErrorType.ValueIsInvalid -> Res.string.invoice_data_error_value_invalid
        InvoiceDataErrorType.CalculatedAmountsAreInvalid -> Res.string.invoice_data_error_calculated_amounts_are_invalid
    }

    HorizontalLabelledValue(fieldName, stringResource(errorMessage, dataError.erroneousValue ?: ""))
}

@Composable
private fun XmlValidationErrorListItem(error: ValidationResultItem) {
    // TODO: get invoice field from BT (if available) or maybe location or test
    Text(error.message, Modifier.padding(top = Style.SectionTopPadding, bottom = 4.dp) // padding = same values as in HorizontalLabelledValue
        .padding(horizontal = ErrorListItemHorizontalPadding), maxLines = 3)
}

@Composable
private fun PdfValidationErrorListItem(error: PdfValidationError) {
    Text("${error.category} ${error.test}, ${error.rule}: ${error.englishMessage}",
        Modifier.padding(top = Style.SectionTopPadding, bottom = 4.dp).padding(horizontal = ErrorListItemHorizontalPadding)) // padding = same values as in HorizontalLabelledValue
}