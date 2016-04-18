package net.ilexiconn.qubble.client.gui.component;

import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TextBoxComponent extends Gui implements IComponent<GuiScreen> {
    private String text;
    private int posX;
    private int posY;
    private int width;
    private int height;
    private boolean selected;
    private int lineScrollOffset;
    private int cursorPosition;
    private int selectionEnd;
    private int cursorCounter;

    public TextBoxComponent(String defaultText, int posX, int posY, int width, int height) {
        this.text = defaultText;
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
    }

    @Override
    public void update(GuiScreen gui) {
        this.cursorCounter++;
    }

    @Override
    public void render(GuiScreen gui, float mouseX, float mouseY, double offsetX, double offsetY, float partialTicks) {
        this.drawGradientRect(this.posX + 1, this.posY + 1, this.posX + this.width - 1, this.posY + this.height - 1, QubbleGUI.getPrimaryColor(), selected ? QubbleGUI.getSecondaryColor() : QubbleGUI.getPrimaryColor());
        QubbleGUI.drawOutline(this.posX, this.posY, this.width, this.height, Qubble.CONFIG.getAccentColor(), 1);
        int cursor = this.cursorPosition - this.lineScrollOffset;
        int cursorEnd = this.selectionEnd - this.lineScrollOffset;
        String string = gui.mc.fontRendererObj.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.width);
        boolean verticalCursor = cursor >= 0 && cursor <= string.length();
        boolean renderCursor = this.selected && this.cursorCounter / 6 % 2 == 0 && verticalCursor;
        int x = this.posX;
        int y = this.posY;
        int line = x;

        if (cursorEnd > string.length()) {
            cursorEnd = string.length();
        }

        if (!string.isEmpty()) {
            String s = verticalCursor ? string.substring(0, cursor) : string;
            line = gui.mc.fontRendererObj.drawString(s, x + 3, y + height / 2 - gui.mc.fontRendererObj.FONT_HEIGHT / 2, QubbleGUI.getTextColor());
        }

        boolean renderVerticalCursor = this.cursorPosition < this.text.length();
        int lineX = line;

        if (!verticalCursor) {
            lineX = cursor > 0 ? x + this.width : x;
        } else if (renderVerticalCursor) {
            lineX = line - 1;
            --line;
        }

        if (!string.isEmpty() && verticalCursor && cursor < string.length()) {
            gui.mc.fontRendererObj.drawString(string.substring(cursor), line + 1, y + height / 2 - gui.mc.fontRendererObj.FONT_HEIGHT / 2, QubbleGUI.getTextColor());
        }

        if (renderCursor) {
            if (renderVerticalCursor) {
                Gui.drawRect(lineX, y + 2, lineX + 1, y + height / 2 - gui.mc.fontRendererObj.FONT_HEIGHT / 2 + 1 + gui.mc.fontRendererObj.FONT_HEIGHT, Qubble.CONFIG.getAccentColor());
            } else {
                gui.mc.fontRendererObj.drawString("_", lineX, y + height / 2 - gui.mc.fontRendererObj.FONT_HEIGHT / 2, Qubble.CONFIG.getAccentColor());
            }
        }

        if (cursorEnd != cursor) {
            int l1 = x + gui.mc.fontRendererObj.getStringWidth(string.substring(0, cursorEnd));
            this.drawCursorVertical(lineX + (selectionEnd > cursorPosition ? 0 : 1), y + 1, l1 + (selectionEnd < cursorPosition ? 2 : 3), y + height / 2 - gui.mc.fontRendererObj.FONT_HEIGHT / 2 + 2 + gui.mc.fontRendererObj.FONT_HEIGHT);
        }
    }

    @Override
    public void renderAfter(GuiScreen gui, float mouseX, float mouseY, double offsetX, double offsetY, float partialTicks) {
    }

    @Override
    public boolean mouseClicked(GuiScreen gui, float mouseX, float mouseY, int button) {
        this.selected = this.isMouseSelecting(mouseX, mouseY);
        if (this.selected && button == 0) {
            int i = (int) (mouseX - this.posX - 1);
            String s = gui.mc.fontRendererObj.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.width);
            this.setCursorPosition(gui.mc.fontRendererObj.trimStringToWidth(s, i).length() + this.lineScrollOffset);
        }
        return false;
    }

    @Override
    public boolean mouseDragged(GuiScreen gui, float mouseX, float mouseY, int button, long timeSinceClick) {
        return false;
    }

    @Override
    public boolean mouseReleased(GuiScreen gui, float mouseX, float mouseY, int button) {
        return false;
    }

    @Override
    public boolean keyPressed(GuiScreen gui, char character, int key) {
        if (!this.selected) {
            return false;
        } else if (GuiScreen.isKeyComboCtrlA(key)) {
            this.setCursorPositionEnd();
            this.setSelectionPos(0);
            return true;
        } else if (GuiScreen.isKeyComboCtrlC(key)) {
            GuiScreen.setClipboardString(this.getSelectedText());
            return true;
        } else if (GuiScreen.isKeyComboCtrlV(key)) {
            this.writeText(GuiScreen.getClipboardString());
            return true;
        } else if (GuiScreen.isKeyComboCtrlX(key)) {
            GuiScreen.setClipboardString(this.getSelectedText());
            this.writeText("");
            return true;
        } else {
            switch (key) {
                case 14:
                    if (GuiScreen.isCtrlKeyDown()) {
                        this.deleteWords(-1);
                    } else {
                        this.deleteFromCursor(-1);
                    }
                    return true;
                case 199:
                    if (GuiScreen.isShiftKeyDown()) {
                        this.setSelectionPos(0);
                    } else {
                        this.setCursorPositionZero();
                    }
                    return true;
                case 203:
                    if (GuiScreen.isShiftKeyDown()) {
                        if (GuiScreen.isCtrlKeyDown()) {
                            this.setSelectionPos(this.getNthWordFromPos(-1, this.selectionEnd));
                        } else {
                            this.setSelectionPos(this.selectionEnd - 1);
                        }
                    } else if (GuiScreen.isCtrlKeyDown()) {
                        this.setCursorPosition(this.getNthWordFromCursor(-1));
                    } else {
                        this.moveCursorBy(-1);
                    }
                    return true;
                case 205:
                    if (GuiScreen.isShiftKeyDown()) {
                        if (GuiScreen.isCtrlKeyDown()) {
                            this.setSelectionPos(this.getNthWordFromPos(1, this.selectionEnd));
                        } else {
                            this.setSelectionPos(this.selectionEnd + 1);
                        }
                    } else if (GuiScreen.isCtrlKeyDown()) {
                        this.setCursorPosition(this.getNthWordFromCursor(1));
                    } else {
                        this.moveCursorBy(1);
                    }
                    return true;
                case 207:
                    if (GuiScreen.isShiftKeyDown()) {
                        this.setSelectionPos(this.text.length());
                    } else {
                        this.setCursorPositionEnd();
                    }
                    return true;
                case 211:
                    if (GuiScreen.isCtrlKeyDown()) {
                        this.deleteWords(1);
                    } else {
                        this.deleteFromCursor(1);
                    }
                    return true;
                default:
                    if (ChatAllowedCharacters.isAllowedCharacter(character)) {
                        this.writeText(Character.toString(character));
                        return true;
                    } else {
                        return false;
                    }
            }
        }
    }

    private boolean isMouseSelecting(float mouseX, float mouseY) {
        return mouseX >= this.posX && mouseX < this.posX + this.width && mouseY >= this.posY && mouseY < this.posY + this.height;
    }

    public String getText() {
        return this.text;
    }

    public String getSelectedText() {
        int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
        int j = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
        return this.text.substring(i, j);
    }

    public void writeText(String text) {
        String s = "";
        String s1 = ChatAllowedCharacters.filterAllowedCharacters(text);
        int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
        int j = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
        int l;

        if (!this.text.isEmpty()) {
            s = s + this.text.substring(0, i);
        }

        s = s + s1;
        l = s1.length();

        if (!this.text.isEmpty() && j < this.text.length()) {
            s = s + this.text.substring(j);
        }

        this.text = s;
        this.moveCursorBy(i - this.selectionEnd + l);
    }

    public void deleteWords(int num) {
        if (!this.text.isEmpty()) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                this.deleteFromCursor(this.getNthWordFromCursor(num) - this.cursorPosition);
            }
        }
    }

    public void deleteFromCursor(int num) {
        if (!this.text.isEmpty()) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                boolean flag = num < 0;
                int i = flag ? this.cursorPosition + num : this.cursorPosition;
                int j = flag ? this.cursorPosition : this.cursorPosition + num;
                String s = "";

                if (i >= 0) {
                    s = this.text.substring(0, i);
                }

                if (j < this.text.length()) {
                    s = s + this.text.substring(j);
                }

                this.text = s;

                if (flag) {
                    this.moveCursorBy(num);
                }
            }
        }
    }

    public int getNthWordFromCursor(int numWords) {
        return this.getNthWordFromPos(numWords, this.cursorPosition);
    }

    public int getNthWordFromPos(int n, int pos) {
        return this.getNthWordFromPosWS(n, pos, true);
    }

    public int getNthWordFromPosWS(int n, int pos, boolean skipWs) {
        int i = pos;
        boolean flag = n < 0;
        int j = Math.abs(n);

        for (int k = 0; k < j; ++k) {
            if (!flag) {
                int l = this.text.length();
                i = this.text.indexOf(32, i);

                if (i == -1) {
                    i = l;
                } else {
                    while (skipWs && i < l && this.text.charAt(i) == 32) {
                        ++i;
                    }
                }
            } else {
                while (skipWs && i > 0 && this.text.charAt(i - 1) == 32) {
                    --i;
                }

                while (i > 0 && this.text.charAt(i - 1) != 32) {
                    --i;
                }
            }
        }

        return i;
    }

    public void moveCursorBy(int num) {
        this.setCursorPosition(this.selectionEnd + num);
    }

    public void setCursorPosition(int pos) {
        this.cursorPosition = pos;
        int i = this.text.length();
        this.cursorPosition = MathHelper.clamp_int(this.cursorPosition, 0, i);
        this.setSelectionPos(this.cursorPosition);
    }

    public void setCursorPositionZero() {
        this.setCursorPosition(0);
    }

    public void setCursorPositionEnd() {
        this.setCursorPosition(this.text.length());
    }

    public void setSelectionPos(int position) {
        int i = this.text.length();

        if (position > i) {
            position = i;
        }

        if (position < 0) {
            position = 0;
        }

        this.selectionEnd = position;

        if (this.lineScrollOffset > i) {
            this.lineScrollOffset = i;
        }

        int j = this.width;
        String s = Minecraft.getMinecraft().fontRendererObj.trimStringToWidth(this.text.substring(this.lineScrollOffset), j);
        int k = s.length() + this.lineScrollOffset;

        if (position == this.lineScrollOffset) {
            this.lineScrollOffset -= Minecraft.getMinecraft().fontRendererObj.trimStringToWidth(this.text, j, true).length();
        }

        if (position > k) {
            this.lineScrollOffset += position - k;
        } else if (position <= this.lineScrollOffset) {
            this.lineScrollOffset -= this.lineScrollOffset - position;
        }

        this.lineScrollOffset = MathHelper.clamp_int(this.lineScrollOffset, 0, i);
    }

    private void drawCursorVertical(int startX, int startY, int endX, int endY) {
        if (startX < endX) {
            int i = startX;
            startX = endX;
            endX = i;
        }

        if (startY < endY) {
            int j = startY;
            startY = endY;
            endY = j;
        }

        if (endX > this.posX + this.width) {
            endX = this.posY + this.width;
        }

        if (startX > this.posX + this.width) {
            startX = this.posY + this.width;
        }

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(GlStateManager.LogicOp.OR_REVERSE);
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION);
        vertexbuffer.pos((double) startX, (double) endY, 0.0D).endVertex();
        vertexbuffer.pos((double) endX, (double) endY, 0.0D).endVertex();
        vertexbuffer.pos((double) endX, (double) startY, 0.0D).endVertex();
        vertexbuffer.pos((double) startX, (double) startY, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.disableColorLogic();
        GlStateManager.enableTexture2D();
    }
}
