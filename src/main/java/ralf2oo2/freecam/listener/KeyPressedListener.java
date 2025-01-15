package ralf2oo2.freecam.listener;

import net.fabricmc.loader.api.FabricLoader;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.modificationstation.stationapi.api.client.event.keyboard.KeyStateChangedEvent;
import org.lwjgl.input.Keyboard;
import ralf2oo2.freecam.Freecam;
import ralf2oo2.freecam.client.gui.GuiSavedCameraLocations;
import ralf2oo2.freecam.registry.KeyBindingRegistry;

import java.util.Arrays;

public class KeyPressedListener {
    private String[] validCharacters = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"}; // For alphabetic character support add '"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "Q", "Y", "Z", '
    @EventListener
    public void keyPressed(KeyStateChangedEvent event) {
        if(event.environment == KeyStateChangedEvent.Environment.IN_GAME) {

            // Input cameraposition name
            if(Freecam.freecamController.savePosition || Freecam.freecamController.loadPosition){
                if(Arrays.stream(validCharacters).anyMatch(Keyboard.getKeyName(Keyboard.getEventKey())::equals) && Keyboard.isKeyDown(Keyboard.getEventKey())){
                    Freecam.freecamController.cameraPositionName += Keyboard.getKeyName(Keyboard.getEventKey());
                }
                if(Keyboard.isKeyDown(Keyboard.KEY_BACK)){
                    if(Freecam.freecamController.cameraPositionName.length() > 0){
                        Freecam.freecamController.cameraPositionName = Freecam.freecamController.cameraPositionName.substring(0, Freecam.freecamController.cameraPositionName.length() - 1);
                    }
                }
                if(Keyboard.isKeyDown(Keyboard.KEY_RETURN)){
                    if(Freecam.freecamController.savePosition){
                        if(Freecam.freecamController.cameraPositionName != ""){
                            Freecam.freecamController.saveCameraPosition(Freecam.freecamController.cameraPositionName);
                        }
                        Freecam.freecamController.savePosition = false;
                    }
                    else {
                        Freecam.freecamController.loadCameraPosition(Freecam.freecamController.cameraPositionName);
                        Freecam.freecamController.loadPosition = false;
                    }
                    Freecam.freecamController.cameraPositionName = "";
                }
            }

            // Toggle freecam
            if(Keyboard.isKeyDown(KeyBindingRegistry.freecamKeybinding.code)) {
                ClientPlayerEntity player = Minecraft.class.cast(FabricLoader.getInstance().getGameInstance()).player;
                if(!Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && !Freecam.freecamController.isActive() || !Freecam.freecamController.cameraPositionSet){
                    Freecam.freecamController.setCameraPositionAndRotation(player.x, player.y, player.z, player.pitch, player.yaw + 180, 0);
                    Freecam.freecamController.cameraPositionSet = true;
                }
                if(!Freecam.freecamController.isActive()){
                    Freecam.freecamController.velocityX = 0;
                    Freecam.freecamController.velocityY = 0;
                    Freecam.freecamController.velocityZ = 0;
                }
                Freecam.freecamController.setActive(!Freecam.freecamController.isActive());
            }

            // Toggle player movement
            if(Keyboard.isKeyDown(KeyBindingRegistry.playerMovementKeybinding.code)) {
                Freecam.freecamController.allowPlayerMovement = !Freecam.freecamController.allowPlayerMovement;
            }

            // Change speed
            if(Keyboard.isKeyDown(KeyBindingRegistry.changeSpeedKeybinding.code)) {
                Freecam.freecamController.updateSpeed = true;
            } else {
                Freecam.freecamController.updateSpeed = false;
            }

            // Save/load cameraposition
            if(Keyboard.isKeyDown(KeyBindingRegistry.cameraPositionKeybinding.code)) {
                if(!Freecam.freecamController.loadPosition && !Freecam.freecamController.savePosition && Freecam.freecamController.isActive()){
                    if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)){
                        Freecam.freecamController.savePosition = true;
                    } else {
                        Freecam.freecamController.loadPosition = true;
                    }
                }
            }

            // Open cameraposition gui
            if(Keyboard.isKeyDown(KeyBindingRegistry.cameraPositionGuiKeybinding.code)) {
                if(Freecam.freecamController.isActive()){
                    ((Minecraft) FabricLoader.getInstance().getGameInstance()).setScreen(new GuiSavedCameraLocations());
                }
            }
        }
    }
}
