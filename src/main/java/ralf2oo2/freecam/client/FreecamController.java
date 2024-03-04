package ralf2oo2.freecam.client;

import cyclops.data.Tree;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import ralf2oo2.freecam.Freecam;
import ralf2oo2.freecam.mixin.WorldAccessor;
import ralf2oo2.freecam.util.CameraPosition;
import ralf2oo2.freecam.util.SavedCameraPosition;
import ralf2oo2.freecam.util.SavedCameraPositions;

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

    public boolean isActive(){
        return active;
    }
    public void setActive(boolean active){
        this.active = active;
        this.allowPlayerMovement = false;
    }

    public CameraPosition getCameraPosition(){
        return cameraPosition;
    }
    public void saveCameraPosition(String name){
        savedCameraPositions.put(name, cameraPosition.clone());
        saveCameraPositionsToFile();
        System.out.println("Saving");
    }
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
    public void loadCameraPosition(String name){
        if(!savedCameraPositions.containsKey(name)){
            return;
        }
        cameraPosition = savedCameraPositions.get(name).clone();
        System.out.println(savedCameraPositions.get(name).x);
        System.out.println("Loading");
    }

    public void removeCameraPosition(String key){
        savedCameraPositions.remove(key);
        saveCameraPositionsToFile();
    }

    public void loadSavedCameraPositions(World world){
        if(world == null || !Freecam.saveManager.hasSavedCameraPositions(world.getSeed(), ((WorldAccessor)world).getProperties().getName())){
            setSavedCameraPositions(new SavedCameraPosition[0]);
            return;
        }
        SavedCameraPosition[] savedCameraPositions = Freecam.saveManager.load(world.getSeed(), ((WorldAccessor)world).getProperties().getName());
        setSavedCameraPositions(savedCameraPositions);
    }

    private void setSavedCameraPositions(SavedCameraPosition[] savedCameraPositions){
        this.savedCameraPositions.clear();
        for(SavedCameraPosition savedCameraPosition : savedCameraPositions){
            this.savedCameraPositions.put(savedCameraPosition.name, savedCameraPosition.cameraPosition);
        }
    }

    public int getSavedCameraPositionCount(){
        return savedCameraPositions.size();
    }
    public TreeMap<String, CameraPosition> getSavedCameraPositions(){
        TreeMap<String, CameraPosition> cameraPositions = new TreeMap<>();
        SortedSet<String> keys = new TreeSet<>(savedCameraPositions.keySet());
        for (String key : keys){
            cameraPositions.put(key, savedCameraPositions.get(key));
        }
        return cameraPositions;
    }



    public void setCameraPositionAndRotation(double x, double y, double z, float pitch, float yaw, float roll) {
        this.cameraPosition.x = x;
        this.cameraPosition.y = y;
        this.cameraPosition.z = z;
        this.cameraPosition.pitch = pitch;
        this.cameraPosition.yaw = yaw;
        this.cameraPosition.roll = roll;
    }
    public void setCameraPosition(double x, double y, double z) {
        this.cameraPosition.x = x;
        this.cameraPosition.y = y;
        this.cameraPosition.z = z;
    }

    public void setCameraRotation(float pitch, float yaw, float roll) {
        this.cameraPosition.pitch = pitch;
        this.cameraPosition.yaw = yaw;
        this.cameraPosition.roll = roll;
    }

    private CameraPosition getRelativeCameraPosition(CameraPosition cameraPosition, LivingEntity player, float f1){
        float f2 = player.eyeHeight - 1.62F;

        double d1 = player.prevX + (player.x - player.prevX) * (double)f1;
        double d2 = player.prevY + (player.y - player.prevY) * (double)f1 - (double)f2;
        double d3 = player.prevZ + (player.z - player.prevZ) * (double)f1;


        CameraPosition relativeCameraPosition = new CameraPosition((cameraPosition.x - d1), (cameraPosition.y - d2), (cameraPosition.z - d3), cameraPosition.pitch, cameraPosition.yaw , cameraPosition.roll);

        return relativeCameraPosition;
    }

    public CameraPosition updateCameraPosition(LivingEntity player, float f1){
        return getRelativeCameraPosition(this.cameraPosition, player, f1);
    }
}
