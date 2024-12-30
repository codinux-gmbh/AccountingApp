package net.codinux.accounting.ui.composables.invoice

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.codinux.accounting.resources.*
import net.codinux.accounting.resources.Res
import net.codinux.accounting.ui.composables.VerticalScrollbar
import net.codinux.accounting.ui.composables.forms.*
import net.codinux.accounting.ui.config.Colors
import net.codinux.accounting.ui.config.DI
import net.codinux.accounting.ui.config.Style
import net.codinux.invoicing.pdf.AmountOfMoney
import net.codinux.invoicing.pdf.PdfInvoiceData
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource


private val formatUtil = DI.formatUtil

@Composable
fun PdfInvoiceDataView(data: PdfInvoiceData) {

    var showPdfText by remember { mutableStateOf(false) }

    val stateVertical = rememberScrollState(0)
    val stateHorizontal = rememberScrollState(0)


    Box(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxWidth().verticalScroll(stateVertical).padding(end = Style.SectionTopPadding)) {
                SelectionContainer(modifier = Modifier.fillMaxWidth()) {
                    Section(Res.string.possible_invoice_data) {
                        Text(stringResource(Res.string.possible_invoice_data_explanation), Modifier.padding(vertical = 6.dp))

                        AmountRow(Res.string.total_amount, data.potentialTotalAmount)

                        AmountRow(Res.string.net_amount, data.potentialNetAmount)

                        AmountRow(Res.string.vat_amount, data.potentialValueAddedTax)

                        StringValueRow(Res.string.vat_rate, data.potentialValueAddedTaxRate?.let { formatUtil.formatPercentage(it) })

                        StringValueRow(Res.string.iban, data.potentialIban)

                        StringValueRow(Res.string.bic, data.potentialBic)
                    }
                }

                Column(Modifier.padding(top = Style.SectionTopPadding)) {
                    Row(Modifier.fillMaxWidth(), Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                        Spacer(Modifier.weight(1f))

                        BooleanOption(Res.string.show_pdf_text, showPdfText, false) { showPdfText = !showPdfText }
                    }

                    if (showPdfText) {
                        SelectionContainer(modifier = Modifier.fillMaxWidth()) {
                            RoundedCornersCard {
                                Column(Modifier.fillMaxWidth().padding(horizontal = 6.dp)) {
                                    Text(data.pdfText, Modifier, fontFamily = FontFamily.Monospace)
                                }
                            }
                        }

                        Spacer(Modifier.height(4.dp).background(Colors.CodinuxSecondaryColor))
                    }
                }
            }

        VerticalScrollbar(stateVertical, Modifier.align(Alignment.CenterEnd).fillMaxHeight())

//                VerticalScrollbar(
//                    modifier = Modifier.align(Alignment.CenterEnd)
//                        .fillMaxHeight(),
//                    adapter = rememberScrollbarAdapter(stateVertical)
//                )
//        HorizontalScrollbar(
//            modifier = Modifier.align(Alignment.BottomStart)
//                .fillMaxWidth()
//                .padding(end = 12.dp),
//            adapter = rememberScrollbarAdapter(stateHorizontal)
//        )
    }
}

@Composable
private fun AmountRow(label: StringResource, amountOfMoney: AmountOfMoney?) {
    StringValueRow(label, amountOfMoney?.let { formatUtil.formatAmountOfMoney(it.amount, it.currency) })
}

@Composable
private fun StringValueRow(label: StringResource, value: String?) {
    Row(Modifier.fillMaxWidth().padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(stringResource(label), Modifier.width(150.dp), Colors.FormLabelTextColor, Style.LabelledValueFontSize, maxLines = 1)

        Spacer(Modifier.weight(1f))

        Text(value ?: "", Modifier.width(100.dp).padding(start = 4.dp), Colors.FormValueTextColor, Style.LabelledValueFontSize, fontFamily = FontFamily.Monospace, textAlign = TextAlign.End, maxLines = 1)
    }
}