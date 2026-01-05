package me.lojosho.hibiscuscommons.packets.data;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter @Setter
public class InventoryClickWrapper {

    private int clickType;
    private int slotNumber;

    public InventoryClickWrapper(@NotNull Integer clickType, @NotNull Integer slotNumber) {
        this.clickType = clickType;
        this.slotNumber = slotNumber;
    }
}
