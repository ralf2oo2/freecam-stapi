package ralf2oo2.freecam;

import net.glasslauncher.mods.gcapi3.api.*;

public class FreecamConfig {
    @ConfigRoot(value = "config", visibleName = "Freecam Config")
    public static ConfigFields config = new ConfigFields();

    public static class ConfigFields {
        @ConfigEntry(
                name = "Freecam Speed",
                description = "Changing this value will change the speed of the freecam",
                maxLength = 1000,
                minLength = 0
        )
        public Float speed = 10f;

        @ConfigEntry(
                name = "Freecam Drag",
                description = "Changing this value will change the drag of the freecam, The lower the drag, the longer it takes for the camera to stop. This option doesn't do anything for classic movement",
                maxLength = 1000,
                minLength = 0
        )
        public Float drag = 4f;

        @ConfigEntry(
                name = "Enable Freecam Collisions"
        )
        public Boolean collision = true;
        @ConfigEntry(
                name = "Show Freecam",
                description = "When this option is enabled, a 3D camera is rendered at the position of the freecam"
        )
        public Boolean showCamera = true;
        @ConfigEntry(
                name = "Classic Freecam Movement",
                description = "When this option is enabled the freecam will move like in older versions of Freecam"
        )
        public Boolean classicMovement = false;
    }
}
