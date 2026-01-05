package me.lojosho.hibiscuscommons.packets.data;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Setter @Getter
public class SlotContentWrapper {

    private Integer windowId;
    private int slot;
    private ItemStack itemStack;

    public SlotContentWrapper(Integer windowId, Integer slot, @NotNull ItemStack itemStack) {
        this.windowId = windowId;
        this.slot = slot;
        this.itemStack = itemStack;
    }
}
