package net.codinux.accounting.domain.invoice.service

import net.codinux.accounting.ui.composables.invoice.model.InvoiceItemViewModel
import net.codinux.invoicing.calculator.AmountsCalculator
import net.codinux.invoicing.model.TotalAmounts

class CalculationService {

    private val calculator = AmountsCalculator()


    suspend fun calculateTotalAmounts(items: List<InvoiceItemViewModel>): TotalAmounts? =
        calculator.calculateTotalAmounts(items.map { it.toInvoiceItemPrice() })

}