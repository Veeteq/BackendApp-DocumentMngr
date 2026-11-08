package com.veeteq.documentmngr.processor;

import com.veeteq.documentmngr.config.DocumentProperties;
import com.veeteq.documentmngr.mapper.DocumentMapper;
import com.veeteq.documentmngr.model.Account;
import com.veeteq.documentmngr.model.Document;
import com.veeteq.documentmngr.model.DocumentType;
import com.veeteq.documentmngr.model.Item;
import com.veeteq.documentmngr.repository.AccountRepository;
import com.veeteq.documentmngr.repository.ItemRepository;
import com.veeteq.documentmngr.rest.dto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransferDocumentProcessorTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private DocumentMapper documentMapper;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private DocumentProperties documentProperties;

    @InjectMocks
    private TransferDocumentProcessor processor;

    @Test
    void shouldProcessTransferDocument() {
        var request = validRequest();

        var sourceAccount = account(1L, "PLN");
        var targetAccount = account(2L, "EUR");
        var transferItem = mock(Item.class);
        var document = mock(Document.class);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(targetAccount));
        when(documentProperties.transferItemId()).thenReturn(10L);
        when(itemRepository.findById(10L)).thenReturn(Optional.of(transferItem));
        when(documentMapper.toEntity(
                eq(request),
                any(TransferData.class)))
                .thenReturn(document);

        var result = processor.process(request);

        assertThat(result).isSameAs(document);

        verify(accountRepository, times(1)).findById(1L);
        verify(accountRepository).findById(2L);

        verify(documentProperties).transferItemId();
        verify(itemRepository).findById(10L);

        var transferDataCaptor = ArgumentCaptor.forClass(TransferData.class);
        verify(documentMapper).toEntity(eq(request), transferDataCaptor.capture());

        var transferData = transferDataCaptor.getValue();
        assertThat(transferData.getSourceAccount()).isSameAs(sourceAccount);
        assertThat(transferData.getTargetAccount()).isSameAs(targetAccount);
        assertThat(transferData.getTransferAmount()).isEqualByComparingTo("1000");
        assertThat(transferData.getTransferItem()).isSameAs(transferItem);
    }

    @Test
    void shouldRejectMissingTransferData() {
        var request = validRequest()
                .transfer(null);

        assertThatThrownBy(() -> processor.process(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Transfer data is required for Transfer documents");

        verifyNoInteractions(accountRepository, itemRepository, documentMapper);
    }

    @Test
    void shouldRejectDocumentItemsForTransfer() {
        var request = validRequest()
                .documentItems(List.of(new DocumentItemRequestDto()
                                .itemType(DocumentItemTypeDto.EXP)
                                .itemId(5L)
                                .itemQuantity(BigDecimal.ONE)
                                .itemPrice(BigDecimal.TEN)
                ));

        assertThatThrownBy(() -> processor.process(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Document items must not be provided for Transfer documents");

        verifyNoInteractions(accountRepository, itemRepository, documentMapper);
    }

    @Test
    void shouldRejectCounterpartyForTransfer() {
        var request = validRequest()
                .counterpartyId(99L);

        assertThatThrownBy(() -> processor.process(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Counterparty must not be provided for Transfer documents");

        verifyNoInteractions(accountRepository, itemRepository, documentMapper);
    }

    @Test
    void shouldRejectSameSourceAndTargetAccount() {
        var request = validRequest()
                .accountId(1L)
                .transfer(new TransferRequestDto()
                        .targetAccountId(1L)
                        .transferAmount(BigDecimal.valueOf(1000)));

        assertThatThrownBy(() -> processor.process(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Source and target accounts must be different");

        verifyNoInteractions(accountRepository, itemRepository, documentMapper);
    }

    @Test
    void shouldRejectRegularToTransferTypeChange() {
        var request = new DocumentRequestDto()
                .documentDate(LocalDate.of(2025, 1, 23))
                .documentType(DocumentTypeDto.TRANSFER)
                .accountId(1L)
                .payment(new PaymentDto()
                        .paymentMethod("BANKTRANSFER")
                        .currencyCode("PLN")
                        .exchangeRate(BigDecimal.ONE))
                .transfer(new TransferRequestDto()
                        .targetAccountId(2L)
                        .transferAmount(BigDecimal.TEN))
                .version(1);

        var document = mock(Document.class);

        when(document.getDocumentType())
                .thenReturn(DocumentType.BILL);

        assertThatThrownBy(() -> processor.process(document, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot change a regular document into a Transfer");

        verify(document).getDocumentType();
        verifyNoInteractions(accountRepository, documentMapper);
    }

    @Test
    void shouldUpdateTransferDocument() {
        var request = validRequest()
                .version(1);

        var sourceAccount = account(1L, "PLN");
        var targetAccount = account(2L, "EUR");
        var transferItem = mock(Item.class);
        var document = mock(Document.class);

        when(document.getDocumentType()).thenReturn(DocumentType.TRANSFER);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(targetAccount));
        when(documentProperties.transferItemId()).thenReturn(10L);
        when(itemRepository.findById(10L)).thenReturn(Optional.of(transferItem));
        when(documentMapper.updateWith(
                eq(document),
                eq(request),
                any(TransferData.class)))
                .thenReturn(document);

        var result = processor.process(document, request);

        assertThat(result).isSameAs(document);

        var captor = ArgumentCaptor.forClass(TransferData.class);
        verify(documentMapper).updateWith(eq(document), eq(request), captor.capture());
        var transferData = captor.getValue();
        assertThat(transferData.getSourceAccount()).isSameAs(sourceAccount);
        assertThat(transferData.getTargetAccount()).isSameAs(targetAccount);
        assertThat(transferData.getTransferItem()).isSameAs(transferItem);
        assertThat(transferData.getTransferAmount()).isEqualByComparingTo("1000");
    }

    @Test
    void shouldRejectMissingVersionOnTransferUpdate() {
        var request = validRequest()
                .version(null);

        var document = mock(Document.class);

        assertThatThrownBy(() -> processor.process(document, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Document version is required for update");

        verifyNoInteractions(accountRepository, itemRepository, documentMapper, documentProperties);
    }

    private Account account(Long id, String currency) {
        return Account.builder()
                .withId(id)
                .withCurrency(Currency.getInstance(currency))
                .build();
    }

    private DocumentRequestDto validRequest() {
        return new DocumentRequestDto()
                .documentDate(LocalDate.of(2025, 1, 23))
                .documentType(DocumentTypeDto.TRANSFER)
                .accountId(1L)
                .payment(new PaymentDto()
                        .paymentMethod("BANKTRANSFER")
                        .currencyCode("EUR")
                        .exchangeRate(BigDecimal.valueOf(4.38)))
                .transfer(new TransferRequestDto()
                        .targetAccountId(2L)
                        .transferAmount(BigDecimal.valueOf(1000)));
    }
}
