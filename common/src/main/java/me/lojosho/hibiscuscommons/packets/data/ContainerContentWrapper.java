package me.lojosho.hibiscuscommons.packets.data;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter @Setter
public class ContainerContentWrapper {

    private Integer windowId;
    private List<ItemStack> slotData;

    public ContainerContentWrapper(@NotNull Integer windowId, @NotNull List<ItemStack> slotData) {
        this.windowId = windowId;
        this.slotData = slotData;
    }
}
