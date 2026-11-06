package com.veeteq.documentmngr.processor;

import com.veeteq.documentmngr.config.DocumentProperties;
import com.veeteq.documentmngr.mapper.DocumentMapper;
import com.veeteq.documentmngr.model.Document;
import com.veeteq.documentmngr.repository.AccountRepository;
import com.veeteq.documentmngr.repository.ItemRepository;
import com.veeteq.documentmngr.rest.dto.DocumentRequestDto;
import com.veeteq.documentmngr.rest.dto.TransferRequestDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service(value = DocumentProcessor.Type.TRANSFER)
public class TransferDocumentProcessor extends BaseDocumentProcessor {

    private final ItemRepository itemRepository;
    private final DocumentProperties documentProperties;

    public TransferDocumentProcessor(AccountRepository accountRepository, DocumentMapper documentMapper, ItemRepository itemRepository, DocumentProperties documentProperties) {
        super(accountRepository, documentMapper);
        this.itemRepository = itemRepository;
        this.documentProperties = documentProperties;
    }

    @Override
    public Document process(DocumentRequestDto documentDto) {
        System.out.println("Processing using " + Type.TRANSFER);
        validateRequest(documentDto);

        var transferData = prepareTransferData(documentDto);
        var document = documentMapper.toEntity(documentDto, transferData);
        return document;
    }

    @Override
    public Document process(Document document, DocumentRequestDto documentDto) {
        validateRequest(document, documentDto);

        var transferData = prepareTransferData(documentDto);
        var updated = documentMapper.updateWith(document, documentDto, transferData);
        return updated;
    }

    @Override
    protected void validateRequest(DocumentRequestDto documentDto) {
        super.validateRequest(documentDto);

        var transferRequest = documentDto.getTransfer();
        if (transferRequest == null) throw new IllegalArgumentException("Transfer data is required for Transfer documents");
    }

    private TransferData prepareTransferData(DocumentRequestDto documentDto) {
        //Extract reference data
        var sourceAccountId = documentDto.getAccountId();
        var transferRequest = documentDto.getTransfer();
        var targetAccountId = transferRequest.getTargetAccountId();

        //Base transfer data
        var transferData = new TransferData();
        var sourceAccount = accountRepository.findById(sourceAccountId).orElseThrow();
        var targetAccount = accountRepository.findById(targetAccountId).orElseThrow();
        var transferItem = itemRepository.findById(documentProperties.transferItemId()).orElseThrow();
        var transferAmount = transferRequest.getTransferAmount();

        //Populate transfer wrapper
        transferData.setSourceAccount(sourceAccount);
        transferData.setTargetAccount(targetAccount);
        transferData.setTransferAmount(transferAmount);
        transferData.setTransferItem(transferItem);

        //Provision data
        if (transferRequest.getProvisionAmount() != null &&
                transferRequest.getProvisionAmount().compareTo(BigDecimal.ZERO) > 0 &&
                transferRequest.getProvisionAt() != null) {
            var provisionAmount = transferRequest.getProvisionAmount();
            var provisionItem = itemRepository.findById(documentProperties.provisionItemId()).orElseThrow();
            var provisionAccount = transferRequest.getProvisionAt().equals(TransferRequestDto.ProvisionAtEnum.SOURCE) ? sourceAccount : targetAccount;

            //Populate transfer wrapper
            transferData.setProvisionAccount(provisionAccount);
            transferData.setProvisionAmount(provisionAmount);
            transferData.setProvisionItem(provisionItem);
        }
        return transferData;
    }

}
