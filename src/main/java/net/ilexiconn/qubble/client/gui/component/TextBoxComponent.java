package net.ilexiconn.qubble.client.gui.component;

import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.ClientProxy;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
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
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

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
        String displayString = gui.mc.fontRendererObj.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.width);
        boolean verticalCursor = cursor >= 0 && cursor <= displayString.length();
        boolean renderCursor = this.selected && this.cursorCounter / 6 % 2 == 0 && verticalCursor;
        int x = this.posX;
        int y = this.posY;
        int line = x;

        if (cursorEnd > displayString.length()) {
            cursorEnd = displayString.length();
        }

        if (!displayString.isEmpty()) {
            String s = verticalCursor ? displayString.substring(0, cursor) : displayString;
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

        if (!displayString.isEmpty() && verticalCursor && cursor < displayString.length()) {
            gui.mc.fontRendererObj.drawString(displayString.substring(cursor), line + 1, y + this.height / 2 - gui.mc.fontRendererObj.FONT_HEIGHT / 2, QubbleGUI.getTextColor());
        }

        if (renderCursor) {
            if (renderVerticalCursor) {
                Gui.drawRect(lineX, y + 2, lineX + 1, y + this.height / 2 - gui.mc.fontRendererObj.FONT_HEIGHT / 2 + 1 + gui.mc.fontRendererObj.FONT_HEIGHT, Qubble.CONFIG.getAccentColor());
            } else {
                gui.mc.fontRendererObj.drawString("_", lineX, y + this.height / 2 - gui.mc.fontRendererObj.FONT_HEIGHT / 2, Qubble.CONFIG.getAccentColor());
            }
        }

        if (cursorEnd != cursor) {
            int selectionWidth = x + gui.mc.fontRendererObj.getStringWidth(displayString.substring(0, cursorEnd));
            this.drawCursorVertical(lineX + (selectionEnd > cursorPosition ? 0 : 1), y + 1, selectionWidth + (selectionEnd < cursorPosition ? 2 : 3), y + height / 2 - gui.mc.fontRendererObj.FONT_HEIGHT / 2 + 2 + gui.mc.fontRendererObj.FONT_HEIGHT);
        }
    }

    @Override
    public void renderAfter(GuiScreen gui, float mouseX, float mouseY, double offsetX, double offsetY, float partialTicks) {
    }

    @Override
    public boolean mouseClicked(GuiScreen gui, float mouseX, float mouseY, int button) {
        this.selected = this.isMouseSelecting(mouseX, mouseY);
        if (this.selected && button == 0) {
            int width = (int) (mouseX - this.posX - 1);
            String displayString = gui.mc.fontRendererObj.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.width);
            this.setCursorPosition(gui.mc.fontRendererObj.trimStringToWidth(displayString, width).length() + this.lineScrollOffset);
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
        }
        if (GuiScreen.isKeyComboCtrlA(key)) {
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
                case Keyboard.KEY_BACK:
                    if (GuiScreen.isCtrlKeyDown()) {
                        this.deleteWords(-1);
                    } else {
                        this.deleteFromCursor(-1);
                    }
                    return true;
                case Keyboard.KEY_HOME:
                    if (GuiScreen.isShiftKeyDown()) {
                        this.setSelectionPos(0);
                    } else {
                        this.setCursorPositionZero();
                    }
                    return true;
                case Keyboard.KEY_LEFT:
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
                case Keyboard.KEY_RIGHT:
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
                case Keyboard.KEY_END:
                    if (GuiScreen.isShiftKeyDown()) {
                        this.setSelectionPos(this.text.length());
                    } else {
                        this.setCursorPositionEnd();
                    }
                    return true;
                case Keyboard.KEY_DELETE:
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
        int start = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
        int end = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
        return this.text.substring(start, end);
    }

    public void writeText(String text) {
        String newText = "";
        String allowedText = ChatAllowedCharacters.filterAllowedCharacters(text);
        int start = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
        int end = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;

        if (!this.text.isEmpty()) {
            newText = newText + this.text.substring(0, start);
        }

        newText = newText + allowedText;

        if (!this.text.isEmpty() && end < this.text.length()) {
            newText = newText + this.text.substring(end);
        }

        this.text = newText;
        this.moveCursorBy(start - this.selectionEnd + allowedText.length());
    }

    public void deleteWords(int amount) {
        if (!this.text.isEmpty()) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                this.deleteFromCursor(this.getNthWordFromCursor(amount) - this.cursorPosition);
            }
        }
    }

    public void deleteFromCursor(int amount) {
        if (!this.text.isEmpty()) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                boolean delete = amount < 0;
                int start = delete ? this.cursorPosition + amount : this.cursorPosition;
                int end = delete ? this.cursorPosition : this.cursorPosition + amount;
                String nextText = "";

                if (start >= 0) {
                    nextText = this.text.substring(0, start);
                }

                if (end < this.text.length()) {
                    nextText = nextText + this.text.substring(end);
                }

                this.text = nextText;

                if (delete) {
                    this.moveCursorBy(amount);
                }
            }
        }
    }

    public int getNthWordFromCursor(int numWords) {
        return this.getNthWordFromPos(numWords, this.cursorPosition);
    }

    public int getNthWordFromPos(int n, int position) {
        return this.getNthWordFromPosWhitespace(n, position, true);
    }

    public int getNthWordFromPosWhitespace(int n, int position, boolean skipWhitespace) {
        int currentPos = position;
        boolean flag = n < 0;
        int j = Math.abs(n);
        for (int k = 0; k < j; ++k) {
            if (!flag) {
                int l = this.text.length();
                currentPos = this.text.indexOf(32, currentPos);
                if (currentPos == -1) {
                    currentPos = l;
                } else {
                    while (skipWhitespace && currentPos < l && this.text.charAt(currentPos) == 32) {
                        ++currentPos;
                    }
                }
            } else {
                while (skipWhitespace && currentPos > 0 && this.text.charAt(currentPos - 1) == 32) {
                    --currentPos;
                }

                while (currentPos > 0 && this.text.charAt(currentPos - 1) != 32) {
                    --currentPos;
                }
            }
        }

        return currentPos;
    }

    public void moveCursorBy(int amount) {
        this.setCursorPosition(this.selectionEnd + amount);
    }

    public void setCursorPosition(int position) {
        this.cursorPosition = position;
        this.cursorPosition = MathHelper.clamp_int(this.cursorPosition, 0, this.text.length());
        this.setSelectionPos(this.cursorPosition);
    }

    public void setCursorPositionZero() {
        this.setCursorPosition(0);
    }

    public void setCursorPositionEnd() {
        this.setCursorPosition(this.text.length());
    }

    public void setSelectionPos(int position) {
        int textLength = this.text.length();

        if (position > textLength) {
            position = textLength;
        } else if (position < 0) {
            position = 0;
        }

        this.selectionEnd = position;

        if (this.lineScrollOffset > textLength) {
            this.lineScrollOffset = textLength;
        }

        String displayText = ClientProxy.MINECRAFT.fontRendererObj.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.width);
        int offset = displayText.length() + this.lineScrollOffset;

        if (position == this.lineScrollOffset) {
            this.lineScrollOffset -= ClientProxy.MINECRAFT.fontRendererObj.trimStringToWidth(this.text, this.width, true).length();
        }

        if (position > offset) {
            this.lineScrollOffset += position - offset;
        } else if (position <= this.lineScrollOffset) {
            this.lineScrollOffset -= this.lineScrollOffset - position;
        }

        this.lineScrollOffset = MathHelper.clamp_int(this.lineScrollOffset, 0, textLength);
    }

    private void drawCursorVertical(int startX, int startY, int endX, int endY) {
        if (startX < endX) {
            int prevStartX = startX;
            startX = endX;
            endX = prevStartX;
        }

        if (startY < endY) {
            int prevStartY = startY;
            startY = endY;
            endY = prevStartY;
        }

        if (endX > this.posX + this.width) {
            endX = this.posY + this.width;
        }

        if (startX > this.posX + this.width) {
            startX = this.posY + this.width;
        }

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();
        GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(GlStateManager.LogicOp.OR_REVERSE);
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        buffer.pos((double) startX, (double) endY, 0.0D).endVertex();
        buffer.pos((double) endX, (double) endY, 0.0D).endVertex();
        buffer.pos((double) endX, (double) startY, 0.0D).endVertex();
        buffer.pos((double) startX, (double) startY, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.disableColorLogic();
        GlStateManager.enableTexture2D();
    }
}
