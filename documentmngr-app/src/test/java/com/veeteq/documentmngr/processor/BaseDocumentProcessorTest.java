package com.veeteq.documentmngr.processor;

import com.veeteq.documentmngr.mapper.DocumentMapper;
import com.veeteq.documentmngr.model.Account;
import com.veeteq.documentmngr.model.Document;
import com.veeteq.documentmngr.model.DocumentType;
import com.veeteq.documentmngr.repository.AccountRepository;
import com.veeteq.documentmngr.rest.dto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BaseDocumentProcessorTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private DocumentMapper documentMapper;

    @InjectMocks
    private BaseDocumentProcessor processor;

    @Test
    void shouldProcessRegularDocument() {
        var request = validRequest();

        var account = Account.builder().withId(1L).withCurrency(Currency.getInstance("PLN")).build();
        var document = mock(Document.class);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(documentMapper.toEntity(request, account)).thenReturn(document);

        var result = processor.process(request);

        assertThat(result).isSameAs(document);
        verify(accountRepository, times(2)).findById(1L);
        verify(documentMapper).toEntity(request, account);
        verifyNoMoreInteractions(accountRepository, documentMapper);
    }

    @Test
    void shouldRejectMissingDocumentType() {
        var request = validRequest().documentType(null);

        assertThatThrownBy(() -> processor.process(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Document type is required");

        verifyNoInteractions(accountRepository, documentMapper);
    }

    @Test
    void shouldRejectMissingAccount() {
        var request = validRequest().accountId(null);

        assertThatThrownBy(() -> processor.process(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Account is required");

        verifyNoInteractions(accountRepository, documentMapper);
    }

    @Test
    void shouldRejectMissingPayment() {
        var request = validRequest().payment(null);

        assertThatThrownBy(() -> processor.process(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Payment data is required");

        verifyNoInteractions(accountRepository, documentMapper);
    }

    @Test
    void shouldRejectTransferDataForRegularDocument() {
        var request = validRequest()
                .transfer(new TransferRequestDto()
                        .targetAccountId(2L)
                        .transferAmount(BigDecimal.TEN));

        assertThatThrownBy(() -> processor.process(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Transfer data must not be provided for regular documents");

        verifyNoInteractions(accountRepository, documentMapper);
    }

    @Test
    void shouldRejectMissingDocumentItems() {
        var request = validRequest().documentItems(null);

        assertThatThrownBy(() -> processor.process(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("At least one document item is required for regular documents");

        verifyNoInteractions(accountRepository, documentMapper);
    }

    @Test
    void shouldRejectEmptyDocumentItems() {
        var request = validRequest().documentItems(List.of());

        assertThatThrownBy(() -> processor.process(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("At least one document item is required for regular documents");

        verifyNoInteractions(accountRepository, documentMapper);
    }

    @Test
    void shouldRejectMissingInvoiceNumber() {
        var request = validRequest()
                .documentType(DocumentTypeDto.INVOICE)
                .invoiceNumber(null);

        assertThatThrownBy(() -> processor.process(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invoice number is required for Invoice documents");

        verifyNoInteractions(accountRepository, documentMapper);
    }

    @Test
    void shouldRejectInvoiceNumberForNonInvoice() {
        var request = validRequest()
                .documentType(DocumentTypeDto.BILL)
                .invoiceNumber("INV/2025/001");

        assertThatThrownBy(() -> processor.process(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invoice number is only allowed for Invoice documents");

        verifyNoInteractions(accountRepository, documentMapper);
    }

    @Test
    void shouldRejectMissingCounterpartyForBill() {
        var request = validRequest()
                .documentType(DocumentTypeDto.BILL)
                .counterpartyId(null);

        assertThatThrownBy(() -> processor.process(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Counterparty is required for Bill and Invoice documents");

        verifyNoInteractions(accountRepository, documentMapper);
    }

    @Test
    void shouldRejectMissingCounterpartyForInvoice() {
        var request = validRequest()
                .documentType(DocumentTypeDto.INVOICE)
                .invoiceNumber("INV/2025/001")
                .counterpartyId(null);

        assertThatThrownBy(() -> processor.process(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Counterparty is required for Bill and Invoice documents");

        verifyNoInteractions(accountRepository, documentMapper);
    }

    @Test
    void shouldRejectMissingDocumentName() {
        var request = validRequest()
                .documentName(null);

        assertThatThrownBy(() -> processor.process(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Document name is required for regular documents");

        verifyNoInteractions(accountRepository, documentMapper);
    }

    @Test
    void shouldRejectInvalidExchangeRate() {
        var request = validRequest();
        request.getPayment().exchangeRate(BigDecimal.ZERO);

        assertThatThrownBy(() -> processor.process(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Exchange rate must be greater than zero");

        verifyNoInteractions(accountRepository, documentMapper);
    }

    @Test
    void shouldRejectMissingPaymentMethod() {
        var request = validRequest();
        request.getPayment().paymentMethod(null);

        assertThatThrownBy(() -> processor.process(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Payment method is required");

        verifyNoInteractions(accountRepository, documentMapper);
    }

    @Test
    void shouldRejectMissingCurrencyCode() {
        var request = validRequest();
        request.getPayment().currencyCode(null);

        assertThatThrownBy(() -> processor.process(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Currency code is required");

        verifyNoInteractions(accountRepository, documentMapper);
    }

    @Test
    void shouldRejectMissingItemId() {
        var request = validRequest();

        request.getDocumentItems().get(0)
                .itemId(null);

        assertThatThrownBy(() -> processor.process(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Item ID is required for every document item");

        verifyNoInteractions(accountRepository, documentMapper);
    }

    @Test
    void shouldRejectMissingItemType() {
        var request = validRequest();

        request.getDocumentItems().get(0)
                .itemType(null);

        assertThatThrownBy(() -> processor.process(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Item type is required for every document item");

        verifyNoInteractions(accountRepository, documentMapper);
    }

    @Test
    void shouldRejectExchangeRateDifferentFromOneForSameCurrency() {
        var request = validRequest();

        request.getPayment()
                .currencyCode("PLN")
                .exchangeRate(BigDecimal.valueOf(1.1));

        var account = Account.builder()
                .withId(1L)
                .withCurrency(Currency.getInstance("PLN"))
                .build();

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        assertThatThrownBy(() -> processor.process(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Exchange rate must be 1 when document currency matches account currency");

        verify(accountRepository).findById(1L);
        verifyNoInteractions(documentMapper);
    }

    @Test
    void shouldUpdateRegularDocument() {
        var request = validRequest()
                .version(1);

        var account = Account.builder()
                .withId(1L)
                .withCurrency(Currency.getInstance("PLN"))
                .build();

        var document = mock(Document.class);

        when(document.getDocumentType()).thenReturn(DocumentType.BILL);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(documentMapper.updateWith(document, request, account)).thenReturn(document);

        var result = processor.process(document, request);

        assertThat(result).isSameAs(document);
        verify(document).getDocumentType();
        verify(accountRepository, times(2)).findById(1L);
        verify(documentMapper).updateWith(document, request, account);
    }

    @Test
    void shouldRejectMissingVersionOnUpdate() {
        var request = validRequest()
                .version(null);

        var account = Account.builder().withId(1L).withCurrency(Currency.getInstance("PLN")).build();
        var document = mock(Document.class);
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> processor.process(document, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Document version is required for update");

        verify(accountRepository).findById(1L);
        verifyNoInteractions(documentMapper);
    }

    @Test
    void shouldRejectTransferToRegularTypeChange() {
        var request = validRequest()
                .documentType(DocumentTypeDto.BILL)
                .version(1);

        var account = Account.builder().withId(1L).withCurrency(Currency.getInstance("PLN")).build();
        var document = mock(Document.class);
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));
        when(document.getDocumentType()).thenReturn(DocumentType.TRANSFER);

        assertThatThrownBy(() -> processor.process(document, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot change a Transfer document into another document type");

        verify(document).getDocumentType();
        verify(accountRepository).findById(1L);
        verifyNoInteractions(documentMapper);
    }

    private DocumentRequestDto validRequest() {
        return new DocumentRequestDto()
                .documentDate(LocalDate.of(2025, 1, 23))
                .documentType(DocumentTypeDto.BILL)
                .documentName("Electricity")
                .accountId(1L)
                .counterpartyId(1L)
                .payment(new PaymentDto()
                        .paymentMethod("BANKTRANSFER")
                        .currencyCode("PLN")
                        .exchangeRate(BigDecimal.ONE))
                .documentItems(List.of(
                        new DocumentItemRequestDto()
                                .itemType(DocumentItemTypeDto.EXP)
                                .itemId(5L)
                                .itemQuantity(BigDecimal.ONE)
                                .itemPrice(BigDecimal.TEN)
                ));
    }
}
