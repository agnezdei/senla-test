package com.oskin.task3.production;

public interface IProduct {
    void installFirstPart(IProductPart part);
    void installSecondPart(IProductPart part);
    void installThirdPart(IProductPart part);
    String getDescription();
}