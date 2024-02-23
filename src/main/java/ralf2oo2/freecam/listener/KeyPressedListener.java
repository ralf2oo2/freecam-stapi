package ralf2oo2.freecam.listener;

import net.fabricmc.loader.api.FabricLoader;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.modificationstation.stationapi.api.client.event.keyboard.KeyStateChangedEvent;
import org.lwjgl.input.Keyboard;
import ralf2oo2.freecam.Freecam;
import ralf2oo2.freecam.registry.KeyBindingRegistry;

public class KeyPressedListener {
    @EventListener
    public void keyPressed(KeyStateChangedEvent event) {
        if(event.environment == KeyStateChangedEvent.Environment.IN_GAME) {
            if(Keyboard.isKeyDown(KeyBindingRegistry.freecamKeybinding.code)) {
                Freecam.freecamController.setActive(!Freecam.freecamController.isActive());
                ClientPlayerEntity player = Minecraft.class.cast(FabricLoader.getInstance().getGameInstance()).player;
                Freecam.freecamController.setCameraPositionAndRotation(player.x, player.y + player.eyeHeight, player.z, player.pitch, player.yaw + 180, 0);
                System.out.println("ee");
            }
            if(Keyboard.isKeyDown(KeyBindingRegistry.playerMovementKeybinding.code)) {
                Freecam.freecamController.allowPlayerMovement = !Freecam.freecamController.allowPlayerMovement;
            }
            if(Keyboard.isKeyDown(KeyBindingRegistry.changeSpeedKeybinding.code)) {
                Freecam.freecamController.updateSpeed = true;
            } else {
                Freecam.freecamController.updateSpeed = false;
            }
        }
    }
}
