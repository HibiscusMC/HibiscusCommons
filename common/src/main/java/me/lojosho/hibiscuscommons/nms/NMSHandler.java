package me.lojosho.hibiscuscommons.nms;

import lombok.Getter;

public class NMSHandler {

    private static NMSHandler instance;
    @Getter
    private NMSUtils utilHandler;
    @Getter
    private NMSPacketBuilder packetBuilder;
    @Getter
    private NMSPacketSender packetSender;

    public NMSHandler(NMSUtils utilHandler, NMSPacketBuilder packetBuilder, NMSPacketSender packetSender) {
        if (instance != null) {
            throw new IllegalStateException("NMSHandler is already initialized.");
        }
        this.utilHandler = utilHandler;
        this.packetBuilder = packetBuilder;
        this.packetSender = packetSender;

        instance = this;
    }
}
