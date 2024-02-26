package ralf2oo2.freecam.client.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.render.Tessellator;
import ralf2oo2.freecam.Freecam;
import ralf2oo2.freecam.util.CameraPosition;

import java.text.DecimalFormat;
import java.util.TreeMap;

public class GuiSavedCameraLocations extends Screen {
    private int selectedIndex;
    private CameraLocationListWidget cameraLocationListWidget;
    private TreeMap<String, CameraPosition> cameraPositions;
    private ButtonWidget deleteButton;
    private ButtonWidget teleportButton;
    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        this.cameraLocationListWidget.render(mouseX, mouseY, delta);
        this.drawCenteredTextWithShadow(this.textRenderer, "Saved Camera Positions", this.width / 2, 20, 0xFFFFFF);
        super.render(mouseX, mouseY, delta);
    }

    @Override
    public void init() {
        cameraLocationListWidget = new CameraLocationListWidget();
        cameraPositions = Freecam.freecamController.getSavedCameraPositions();
        registerButtons();
    }

    public void registerButtons(){
        teleportButton = new ButtonWidget(0, this.width / 2 - 154, this.height - 28, 70, 20, "Go To");
        teleportButton.active = false;
        this.buttons.add(teleportButton);
        deleteButton = new ButtonWidget(1, this.width / 2 - 74, this.height - 28, 70, 20, "Delete");
        deleteButton.active = false;
        this.buttons.add(deleteButton);
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if (!button.active) {
            return;
        }
        if(button.id == 0){
            String key = (String)cameraPositions.keySet().toArray()[selectedIndex];
            Freecam.freecamController.loadCameraPosition(key);
            minecraft.setScreen(null);
        }
        if(button.id == 1){
            String key = (String)cameraPositions.keySet().toArray()[selectedIndex];
            Freecam.freecamController.removeCameraPosition(key);
            cameraPositions = Freecam.freecamController.getSavedCameraPositions();
            if(Freecam.freecamController.getSavedCameraPositionCount() == 0){
                setActiveState(false);
            }
        }
    }

    private void setActiveState(boolean active){
        ((ButtonWidget)GuiSavedCameraLocations.this.buttons.get(0)).active = active;
        ((ButtonWidget)GuiSavedCameraLocations.this.buttons.get(1)).active = active;
    }

    public class CameraLocationListWidget extends EntryListWidget {
        public CameraLocationListWidget() {
            super(GuiSavedCameraLocations.this.minecraft, GuiSavedCameraLocations.this.width, GuiSavedCameraLocations.this.height, 32, GuiSavedCameraLocations.this.height - 32, 26);
        }

        @Override
        protected int getEntryCount() {
            return Freecam.freecamController.getSavedCameraPositionCount();
        }

        @Override
        protected void entryClicked(int index, boolean doubleClick) {
            selectedIndex = index;
        }

        @Override
        protected boolean isSelectedEntry(int index) {
            setActiveState(true);
            return index == selectedIndex;
        }

        @Override
        protected int getEntriesHeight() {
            return Freecam.freecamController.getSavedCameraPositionCount() * 26;
        }

        @Override
        protected void renderBackground() {
            GuiSavedCameraLocations.this.renderBackground();
        }

        @Override
        protected void renderEntry(int index, int x, int y, int l, Tessellator tesselator) {
            String key = (String)cameraPositions.keySet().toArray()[index];
            CameraPosition cameraPosition = cameraPositions.get(key);
            DecimalFormat df = new DecimalFormat("#.#");
            String positionString = "X: " + df.format(cameraPosition.x) + " Y: " + df.format(cameraPosition.y) + " Z: " + df.format(cameraPosition.z); //+ " - RX: " + df.format(cameraPosition.pitch) + " RY: " + df.format(cameraPosition.yaw) + " RZ: " + df.format(cameraPosition.roll);
            GuiSavedCameraLocations.this.drawTextWithShadow(GuiSavedCameraLocations.this.textRenderer, "Camera Position " + key, x + 2, y + 1, 0xFFFFFF);
            GuiSavedCameraLocations.this.drawTextWithShadow(GuiSavedCameraLocations.this.textRenderer, positionString, x + 2, y + 12, 0xFFFFFF);
        }
    }
}
