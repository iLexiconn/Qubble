package net.ilexiconn.qubble.client.gui;

import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.component.ButtonComponent;
import net.ilexiconn.qubble.client.gui.component.IActionHandler;
import net.ilexiconn.qubble.client.gui.component.IGUIComponent;
import net.ilexiconn.qubble.client.gui.component.ModelViewComponent;
import net.ilexiconn.qubble.client.gui.dialog.Dialog;
import net.ilexiconn.qubble.server.model.QubbleModel;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class QubbleGUI extends GuiScreen {
    public static final int PRIMARY_COLOR = 0xFF3D3D3D;
    public static final int SECONDARY_COLOR = 0xFF212121;

    private GuiMainMenu mainMenu;

    private List<Dialog> openDialogs = new ArrayList<>();
    private List<IGUIComponent> components = new ArrayList<>();

    private QubbleModel currentModel;

    public QubbleGUI(GuiMainMenu mainMenu) {
        this.mainMenu = mainMenu;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.components.clear();
        this.openDialogs.clear();
        this.components.add(new ButtonComponent("x", 0, 0, 20, 20, "Close Qubble and return to the main menu", (gui, component) -> ClientProxy.MINECRAFT.displayGuiScreen(QubbleGUI.this.mainMenu)));
        this.components.add(new ModelViewComponent());
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        for (IGUIComponent component : new ArrayList<>(this.components)) {
            component.render(this, mouseX, mouseY, partialTicks);
        }
        for (Dialog dialog : new ArrayList<>(this.openDialogs)) {
            dialog.render(this, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        for (IGUIComponent component : new ArrayList<>(this.components)) {
            component.mouseDragged(this, mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        }
        for (Dialog dialog : new ArrayList<>(this.openDialogs)) {
            dialog.mouseDragged(this, mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        super.mouseClicked(mouseX, mouseY, button);
        for (IGUIComponent component : new ArrayList<>(this.components)) {
            component.mouseClicked(this, mouseX, mouseY, button);
        }
        for (Dialog dialog : new ArrayList<>(this.openDialogs)) {
            dialog.mouseClicked(this, mouseX, mouseY, button);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);
        for (Dialog dialog : new ArrayList<>(this.openDialogs)) {
            dialog.mouseReleased(this, mouseX, mouseY, button);
        }
    }

    private void drawBackground() {
        GlStateManager.disableTexture2D();
        GlStateManager.color(0.05F, 0.05F, 0.05F, 1.0F);
        this.drawTexturedModalRect(0, 0, 0, 0, this.width, this.height);
        GlStateManager.enableTexture2D();
    }

    public void drawRectangle(int x, int y, int width, int height, int color) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        float a = (float)(color >> 24 & 0xFF) / 255.0F;
        float r = (float)(color >> 16 & 0xFF) / 255.0F;
        float g = (float)(color >> 8 & 0xFF) / 255.0F;
        float b = (float)(color & 0xFF) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexBuffer = tessellator.getBuffer();
        vertexBuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vertexBuffer.pos(x, y + height, 0.0).color(r, g, b, a).endVertex();
        vertexBuffer.pos(x + width, y + height, 0.0).color(r, g, b, a).endVertex();
        vertexBuffer.pos(x + width, y, 0.0).color(r, g, b, a).endVertex();
        vertexBuffer.pos(x, y, 0.0).color(r, g, b, a).endVertex();
        tessellator.draw();
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public void drawOutline(int x, int y, int width, int height, int color, int outlineSize) {
        drawRectangle(x, y, width - outlineSize, outlineSize, color);
        drawRectangle(x + width - outlineSize, y, outlineSize, height - outlineSize, color);
        drawRectangle(x, y + height - outlineSize, width, outlineSize, color);
        drawRectangle(x, y, outlineSize, height - outlineSize, color);
    }

    public QubbleModel getCurrentModel() {
        return this.currentModel;
    }

    public void closeDialog(Dialog dialog) {
        this.openDialogs.remove(dialog);
    }
}
