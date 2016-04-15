package net.ilexiconn.qubble.client.gui;

import net.ilexiconn.qubble.client.gui.component.ButtonComponent;
import net.ilexiconn.qubble.client.gui.component.IGuiComponent;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiQubbleEditor extends GuiScreen {
    public static final int PRIMARY_COLOR = 0x3D3D3D;
    public static final int SECONDARY_COLOR = 0x212121;
    public static final int TERTIARY_COLOR = 0x10101;

    private GuiMainMenu menu;

    private List<IGuiComponent> components = new ArrayList<>();

    public GuiQubbleEditor(GuiMainMenu menu) {
        this.menu = menu;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.components.add(new ButtonComponent(10, 10, 100, 20, "Test", () -> System.out.println("Test Button")));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        for (IGuiComponent component : this.components) {
            component.render(this, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        for (IGuiComponent component : this.components) {
            component.mouseDragged(this, mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        super.mouseClicked(mouseX, mouseY, button);
        for (IGuiComponent component : this.components) {
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
