package ralf2oo2.freecam;

import net.minecraft.util.math.Box;
import ralf2oo2.freecam.client.FreecamController;
import ralf2oo2.freecam.client.SaveManager;

public class Freecam {
    public static Box cameraBoundingBox = Box.create(-0.2d, -0.2d, -0.2d, 0.2d, 0.2d, 0.2d);
    public static FreecamController freecamController = new FreecamController();
    public static SaveManager saveManager = new SaveManager();
}
