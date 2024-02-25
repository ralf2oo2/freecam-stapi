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
        list.add(freecamKeybinding = new KeyBinding("key.freecam.freecam", Keyboard.KEY_C));
        list.add(playerMovementKeybinding = new KeyBinding("key.freecam.playermovement", Keyboard.KEY_V));
        list.add(changeSpeedKeybinding = new KeyBinding("key.freecam.changespeed", Keyboard.KEY_B));
        list.add(cameraPositionKeybinding = new KeyBinding("key.freecam.cameraposition", Keyboard.KEY_N));
        list.add(cameraPositionGuiKeybinding = new KeyBinding("key.freecam.camerapositiongui", Keyboard.KEY_M));
    }
}
