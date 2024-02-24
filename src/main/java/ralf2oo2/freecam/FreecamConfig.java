package ralf2oo2.freecam;


import net.glasslauncher.mods.api.gcapi.api.ConfigName;
import net.glasslauncher.mods.api.gcapi.api.MaxLength;

public class FreecamConfig {
    @ConfigName("Freecam Speed")
    @MaxLength(1000)
    public Float speed = 10f;
}
