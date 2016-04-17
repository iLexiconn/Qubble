package net.ilexiconn.qubble.client.gui.component;

import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.List;

@SideOnly(Side.CLIENT)
public class SelectionListComponent extends Gui implements IComponent<GuiScreen> {
    private int posX;
    private int posY;
    private int width;
    private int height;
    private List<String> entries;
    private IActionHandler<GuiScreen, SelectionListComponent> actionHandler;

    private String selected;

    private int scroll;

    private int maxDisplayEntries;
    private int maxScroll;

    private int scrollYOffset;

    private boolean scrolling;

    private float scrollPerEntry;

    public SelectionListComponent(int posX, int posY, int width, int height, List<String> entries, IActionHandler<GuiScreen, SelectionListComponent> actionHandler) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.entries = entries;
        this.actionHandler = actionHandler;
        this.maxDisplayEntries = this.height / 13;
        this.maxScroll = Math.max(0, this.entries.size() - this.maxDisplayEntries);
        this.scrollPerEntry = (float) (this.entries.size()) / (float) (this.height - 2);
    }

    @Override
    public void render(GuiScreen gui, float mouseX, float mouseY, double offsetX, double offsetY, float partialTicks) {
        int primaryColor = QubbleGUI.getPrimaryColor();
        int secondaryColor = QubbleGUI.getSecondaryColor();
        QubbleGUI.drawRectangle(this.posX, this.posY, this.width, this.height, primaryColor);
        QubbleGUI.drawOutline(this.posX, this.posY, this.width, this.height, secondaryColor, 1);
        FontRenderer fontRenderer = ClientProxy.MINECRAFT.fontRendererObj;
        int y = (int) (-this.scroll * this.scrollPerEntry * 13);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        ScaledResolution resolution = new ScaledResolution(ClientProxy.MINECRAFT);
        int scaleFactor = resolution.getScaleFactor();
        GL11.glScissor((int) ((this.posX + offsetX) * scaleFactor), (int) ((gui.height - (this.posY + this.height + offsetY - 1)) * scaleFactor), this.width * scaleFactor, (this.height - 2) * scaleFactor);
        for (String entry : this.entries) {
                int entryX = this.posX + 1;
                int entryY = this.posY + y + 1;
                int entryWidth = this.width - 16;
                int entryHeight = 12;
                boolean selected = this.isSelected(entryX, entryY, entryWidth, entryHeight, mouseX, mouseY);
                this.drawGradientRect(entryX, entryY, entryX + entryWidth, entryY + entryHeight, primaryColor, selected ? secondaryColor : primaryColor);
            QubbleGUI.drawOutline(entryX, entryY, entryWidth, entryHeight, Qubble.CONFIG.getAccentColor(), 1);
                fontRenderer.drawString(entry, entryX + 2, entryY + 2, QubbleGUI.getTextColor());
            y += 13;
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        if (this.maxScroll > 0) {
            int scrollX = this.posX + this.width - 9;
            int scrollY = this.posY + this.scroll + 2;
            int height = (int) ((this.height - 2) / ((float) this.entries.size() / (float) this.maxDisplayEntries));
            QubbleGUI.drawRectangle(scrollX, scrollY, 6, height, this.scrolling ? primaryColor : secondaryColor);
            QubbleGUI.drawOutline(scrollX, scrollY, 6, height, Qubble.CONFIG.getAccentColor(), 1);
        }
    }

    @Override
    public void renderAfter(GuiScreen gui, float mouseX, float mouseY, double offsetX, double offsetY, float partialTicks) {

    }

    @Override
    public boolean mouseClicked(GuiScreen gui, float mouseX, float mouseY, int button) {
        boolean flag = false;
        int y = (int) (-this.scroll * this.scrollPerEntry * 13);
        for (String entry : this.entries) {
            if (y + 13 < this.height && y >= 0) {
                int entryX = this.posX + 1;
                int entryY = this.posY + y + 1;
                int entryWidth = this.width - 16;
                int entryHeight = 12;
                if (this.isSelected(entryX, entryY, entryWidth, entryHeight, mouseX, mouseY)) {
                    this.selected = entry;
                    this.actionHandler.onAction(gui, this);
                    flag = true;
                    break;
                }
            }
            y += 13;
        }
        if (this.maxScroll > 0) {
            int scrollX = this.posX + this.width - 9;
            int scrollY = this.posY + (this.scroll) + 2;
            int height = (int) ((this.height - 2) / ((float) this.entries.size() / (float) this.maxDisplayEntries));
            if (mouseX >= scrollX && mouseX < scrollX + 6 && mouseY >= scrollY && mouseY < scrollY + height) {
                this.scrolling = true;
                this.scrollYOffset = (int) (mouseY - scrollY);
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public boolean mouseDragged(GuiScreen gui, float mouseX, float mouseY, int button, long timeSinceClick) {
        if (this.scrolling) {
            this.scroll = (int) Math.max(0, Math.min(this.maxScroll / this.scrollPerEntry, mouseY - this.posY - 2 - this.scrollYOffset));
        }
        return this.scrolling;
    }

    @Override
    public boolean mouseReleased(GuiScreen gui, float mouseX, float mouseY, int button) {
        this.scrolling = false;
        return false;
    }

    @Override
    public boolean keyPressed(GuiScreen gui, char character, int key) {
        return false;
    }

    private boolean isSelected(int entryX, int entryY, int entryWidth, int entryHeight, float mouseX, float mouseY) {
        return mouseX > entryX && mouseX < entryX + entryWidth && mouseY > entryY && mouseY < entryY + entryHeight;
    }

    public String getSelected() {
        return this.selected;
    }
}
