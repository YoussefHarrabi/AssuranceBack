package com.assurance.assuranceback.Services.FactureService;

import com.assurance.assuranceback.Entity.FactureEntity.Facture;
import com.assurance.assuranceback.Entity.FactureEntity.ResponseMessage;

public interface IEmailService {
    ResponseMessage sendUnpaidInvoiceNotification(Facture facture, String currentDate);

}
