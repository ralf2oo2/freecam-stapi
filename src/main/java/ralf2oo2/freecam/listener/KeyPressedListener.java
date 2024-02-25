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

public class KeyPressedListener {
    @EventListener
    public void keyPressed(KeyStateChangedEvent event) {
        if(event.environment == KeyStateChangedEvent.Environment.IN_GAME) {
            if(Freecam.freecamController.savePosition || Freecam.freecamController.loadPosition){
                if(Keyboard.isKeyDown(Keyboard.KEY_1)){
                    Freecam.freecamController.cameraPositionName += "1";
                }
                if(Keyboard.isKeyDown(Keyboard.KEY_2)){
                    Freecam.freecamController.cameraPositionName += "2";
                }
                if(Keyboard.isKeyDown(Keyboard.KEY_3)){
                    Freecam.freecamController.cameraPositionName += "3";
                }
                if(Keyboard.isKeyDown(Keyboard.KEY_4)){
                    Freecam.freecamController.cameraPositionName += "4";
                }
                if(Keyboard.isKeyDown(Keyboard.KEY_5)){
                    Freecam.freecamController.cameraPositionName += "5";
                }
                if(Keyboard.isKeyDown(Keyboard.KEY_6)){
                    Freecam.freecamController.cameraPositionName += "6";
                }
                if(Keyboard.isKeyDown(Keyboard.KEY_7)){
                    Freecam.freecamController.cameraPositionName += "7";
                }
                if(Keyboard.isKeyDown(Keyboard.KEY_8)){
                    Freecam.freecamController.cameraPositionName += "8";
                }
                if(Keyboard.isKeyDown(Keyboard.KEY_9)){
                    Freecam.freecamController.cameraPositionName += "9";
                }
                if(Keyboard.isKeyDown(Keyboard.KEY_0)){
                    Freecam.freecamController.cameraPositionName += "0";
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
            if(Keyboard.isKeyDown(KeyBindingRegistry.freecamKeybinding.code)) {
                ClientPlayerEntity player = Minecraft.class.cast(FabricLoader.getInstance().getGameInstance()).player;
                if(!Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && !Freecam.freecamController.isActive() || !Freecam.freecamController.cameraPositionSet){
                    Freecam.freecamController.setCameraPositionAndRotation(player.x, player.y + player.eyeHeight, player.z, player.pitch, player.yaw + 180, 0);
                    Freecam.freecamController.cameraPositionSet = true;
                    System.out.println("reset location");
                }
                Freecam.freecamController.setActive(!Freecam.freecamController.isActive());
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
            if(Keyboard.isKeyDown(KeyBindingRegistry.cameraPositionKeybinding.code)) {
                if(!Freecam.freecamController.loadPosition && !Freecam.freecamController.savePosition && Freecam.freecamController.isActive()){
                    if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)){
                        Freecam.freecamController.savePosition = true;
                    } else {
                        Freecam.freecamController.loadPosition = true;
                    }
                }
            }
            if(Keyboard.isKeyDown(KeyBindingRegistry.cameraPositionGuiKeybinding.code)) {
                ((Minecraft) FabricLoader.getInstance().getGameInstance()).setScreen(new GuiSavedCameraLocations());
            }
        }
    }
}
