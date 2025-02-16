package ralf2oo2.freecam.registry;

import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.client.option.KeyBinding;
import net.modificationstation.stationapi.api.client.event.option.KeyBindingRegisterEvent;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class KeyBindingRegistry {
    public static KeyBinding freecamKeybinding;
    public static KeyBinding playerMovementKeybinding;
    public static KeyBinding changeSpeedKeybinding;
    public static KeyBinding cameraPositionKeybinding;
    public static KeyBinding cameraPositionGuiKeybinding;

    @EventListener
    public void registerKeyBindings(KeyBindingRegisterEvent event) {
        List<KeyBinding> list = event.keyBindings;
        list.add(freecamKeybinding = new KeyBinding("Toggle Freecam", Keyboard.KEY_C));
        list.add(playerMovementKeybinding = new KeyBinding("Toggle Player Movement", Keyboard.KEY_V));
        list.add(changeSpeedKeybinding = new KeyBinding("Change Freecam Speed", Keyboard.KEY_B));
        list.add(cameraPositionKeybinding = new KeyBinding("Save Or Load Camera Position", Keyboard.KEY_N));
        list.add(cameraPositionGuiKeybinding = new KeyBinding("Open Camera Position Gui", Keyboard.KEY_M));
    }
}
