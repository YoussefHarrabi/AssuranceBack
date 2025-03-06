package com.assurance.assuranceback.Services.FactureService;


import com.google.zxing.WriterException;

import java.io.IOException;

public interface IQrCodeService {
    byte[] generateQrCode(String text, int width, int height) throws WriterException, IOException;
}