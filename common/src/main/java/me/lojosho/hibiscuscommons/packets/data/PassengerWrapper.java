package me.lojosho.hibiscuscommons.packets.data;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter @Setter
public class PassengerWrapper {

    private int owner;
    private List<Integer> passengers;

    public PassengerWrapper(@NotNull Integer owner, @NotNull List<Integer> passengers) {
        this.owner = owner;
        this.passengers = passengers;
    }
}
