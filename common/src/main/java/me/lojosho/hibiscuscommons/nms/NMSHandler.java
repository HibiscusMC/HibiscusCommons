package me.lojosho.hibiscuscommons.nms;

import lombok.Getter;

public class NMSHandler {

    private static NMSHandler instance;
    @Getter
    private NMSUtils utilHandler;
    @Getter
    private NMSPackets packetHandler;

    public NMSHandler(NMSUtils utilHandler, NMSPackets packetHandler) {
        if (instance != null) {
            throw new IllegalStateException("NMSHandler is already initialized.");
        }
        this.utilHandler = utilHandler;
        this.packetHandler = packetHandler;

        instance = this;
    }
}
