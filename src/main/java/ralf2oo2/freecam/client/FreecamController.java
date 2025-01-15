package ralf2oo2.freecam.client;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import ralf2oo2.freecam.Freecam;
import ralf2oo2.freecam.mixin.WorldAccessor;
import ralf2oo2.freecam.util.CameraPosition;
import ralf2oo2.freecam.util.SavedCameraPosition;

import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class FreecamController {
    private boolean active;
    private CameraPosition cameraPosition = new CameraPosition();
    private TreeMap<String, CameraPosition> savedCameraPositions = new TreeMap<>();
    private Minecraft minecraft;
    public float move = 0f;
    public float strafe = 0f;
    public double velocityX;
    public double velocityY;
    public double velocityZ;
    public boolean cameraPositionSet = false;
    public boolean jumping = false;
    public boolean sneaking = false;
    public boolean allowPlayerMovement = false;
    public boolean updateSpeed = false;
    public boolean savePosition = false;
    public boolean loadPosition = false;
    public String cameraPositionName = "";

    public FreecamController() {
        minecraft = Minecraft.class.cast(FabricLoader.getInstance().getGameInstance());
    }

    // Return if the freecam is active or not
    public boolean isActive(){
        return active;
    }

    // Set the freecam active state
    public void setActive(boolean active){
        this.active = active;
        this.allowPlayerMovement = false;
    }

    // Return freecam position
    public CameraPosition getCameraPosition(){
        return cameraPosition;
    }

    // Save cameraposition
    public void saveCameraPosition(String name){
        savedCameraPositions.put(name, cameraPosition.clone());
        saveCameraPositionsToFile();
    }

    // Save camerapositions to json file
    private void saveCameraPositionsToFile(){
        Minecraft minecraft = Minecraft.class.cast(FabricLoader.getInstance().getGameInstance());
        TreeMap<String, CameraPosition> cameraPositionTreeMap = getSavedCameraPositions();
        String[] keys = cameraPositionTreeMap.keySet().toArray(new String[0]);
        SavedCameraPosition[] cameraPositions = new SavedCameraPosition[keys.length];
        int index = 0;
        for(String key : keys){
            cameraPositions[index] = new SavedCameraPosition();
            cameraPositions[index].cameraPosition = cameraPositionTreeMap.get(key);
            cameraPositions[index].name = key;
            index++;
        }
        Freecam.saveManager.save(minecraft.world.getSeed(), ((WorldAccessor)minecraft.world).getProperties().getName(), cameraPositions);
    }

    // Load cameraposition from map
    public void loadCameraPosition(String name){
        if(!savedCameraPositions.containsKey(name)){
            return;
        }
        cameraPosition = savedCameraPositions.get(name).clone();
    }

    // Remove cameraposition from map
    public void removeCameraPosition(String key){
        savedCameraPositions.remove(key);
        saveCameraPositionsToFile();
    }

    // Load camerapositions from json file
    public void loadSavedCameraPositions(World world){
        if(world == null || !Freecam.saveManager.hasSavedCameraPositions(world.getSeed(), ((WorldAccessor)world).getProperties().getName())){
            setSavedCameraPositions(new SavedCameraPosition[0]);
            return;
        }
        SavedCameraPosition[] savedCameraPositions = Freecam.saveManager.load(world.getSeed(), ((WorldAccessor)world).getProperties().getName());
        setSavedCameraPositions(savedCameraPositions);
    }

    // Clear saved camerapositions map and fill with savedcamerapositions array
    private void setSavedCameraPositions(SavedCameraPosition[] savedCameraPositions){
        this.savedCameraPositions.clear();
        for(SavedCameraPosition savedCameraPosition : savedCameraPositions){
            this.savedCameraPositions.put(savedCameraPosition.name, savedCameraPosition.cameraPosition);
        }
    }

    // Get saved camerapositions count
    public int getSavedCameraPositionCount(){
        return savedCameraPositions.size();
    }

    // Get saved camerapositions in a sorted treemap
    public TreeMap<String, CameraPosition> getSavedCameraPositions(){
        TreeMap<String, CameraPosition> cameraPositions = new TreeMap<>();
        SortedSet<String> keys = new TreeSet<>(savedCameraPositions.keySet());
        for (String key : keys){
            cameraPositions.put(key, savedCameraPositions.get(key));
        }
        return cameraPositions;
    }

    // Set camera position and rotation
    public void setCameraPositionAndRotation(double x, double y, double z, float pitch, float yaw, float roll) {
        this.cameraPosition.x = x;
        this.cameraPosition.y = y;
        this.cameraPosition.z = z;
        this.cameraPosition.pitch = pitch;
        this.cameraPosition.yaw = yaw;
        this.cameraPosition.roll = roll;
    }

    // Set camera position
    public void setCameraPosition(double x, double y, double z) {
        this.cameraPosition.x = x;
        this.cameraPosition.y = y;
        this.cameraPosition.z = z;
    }

    // Set camera rotation
    public void setCameraRotation(float pitch, float yaw, float roll) {
        this.cameraPosition.pitch = pitch;
        this.cameraPosition.yaw = yaw;
        this.cameraPosition.roll = roll;
    }

    // Get camera position relative from player
    public CameraPosition getRelativeCameraPosition(CameraPosition cameraPosition, LivingEntity player, float f1){
        float f2 = player.standingEyeHeight - 1.62F;

        double d1 = player.prevX + (player.x - player.prevX) * (double)f1;
        double d2 = player.prevY + (player.y - player.prevY) * (double)f1 - (double)f2;
        double d3 = player.prevZ + (player.z - player.prevZ) * (double)f1;


        CameraPosition relativeCameraPosition = new CameraPosition((cameraPosition.x - d1), (cameraPosition.y - d2), (cameraPosition.z - d3), cameraPosition.pitch, cameraPosition.yaw , cameraPosition.roll);

        return relativeCameraPosition;
    }

    // Update cameraposition
    public CameraPosition updateCameraPosition(LivingEntity player, float f1){
        return getRelativeCameraPosition(this.cameraPosition, player, f1);
    }
}
