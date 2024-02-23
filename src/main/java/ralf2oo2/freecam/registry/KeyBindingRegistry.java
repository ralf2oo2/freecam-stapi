package ralf2oo2.freecam.registry;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.client.option.KeyBinding;
import net.modificationstation.stationapi.api.client.event.option.KeyBindingRegisterEvent;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class KeyBindingRegistry {
    public static KeyBinding freecamKeybinding;
    @EventListener
    public void registerKeyBindings(KeyBindingRegisterEvent event) {
        List<KeyBinding> list = event.keyBindings;
        list.add(freecamKeybinding = new KeyBinding("key.freecam.freecam", Keyboard.KEY_C));
    }
}
