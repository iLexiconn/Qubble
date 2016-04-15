package net.ilexiconn.qubble.client.gui;

import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.component.ButtonComponent;
import net.ilexiconn.qubble.client.gui.component.IComponent;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QubbleGUI extends GuiScreen {
    public static final int PRIMARY_COLOR = 0xFF3D3D3D;
    public static final int SECONDARY_COLOR = 0xFF212121;

    private GuiMainMenu mainMenu;

    private List<IComponent> components = new ArrayList<>();

    public QubbleGUI(GuiMainMenu mainMenu) {
        this.mainMenu = mainMenu;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.components.add(new ButtonComponent(0, 0, 20, 20, "x", () -> ClientProxy.MINECRAFT.displayGuiScreen(this.mainMenu)));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        for (IComponent component : this.components) {
            component.render(this, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        for (IComponent component : this.components) {
            component.mouseDragged(this, mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        super.mouseClicked(mouseX, mouseY, button);
        for (IComponent component : this.components) {
            component.mouseClicked(this, mouseX, mouseY, button);
        }
    }

    private void drawBackground() {
        GlStateManager.disableTexture2D();
        GlStateManager.color(0.05F, 0.05F, 0.05F, 1.0F);
        this.drawTexturedModalRect(0, 0, 0, 0, this.width, this.height);
        GlStateManager.enableTexture2D();
    }
}
