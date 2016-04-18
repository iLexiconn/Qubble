package net.ilexiconn.qubble.client.gui.dialog;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

@SideOnly(Side.CLIENT)
public enum DialogHandler {
    INSTANCE;

    private Map<GuiScreen, List<Dialog<?>>> dialogMap = new WeakHashMap<>();

    public <T extends GuiScreen> void clearGUI(T gui) {
        if (this.dialogMap.containsKey(gui)) {
            this.dialogMap.get(gui).clear();
        }
    }

    public <T extends GuiScreen> void openDialog(T gui, Dialog<T> dialog) {
        if (this.dialogMap.containsKey(gui)) {
            this.dialogMap.get(gui).add(dialog);
        } else {
            List<Dialog<?>> dialogMap = new ArrayList<>();
            dialogMap.add(dialog);
            this.dialogMap.put(gui, dialogMap);
        }
    }

    public <T extends GuiScreen> void closeDialog(T gui, Dialog<T> dialog) {
        if (this.dialogMap.containsKey(gui)) {
            this.dialogMap.get(gui).remove(dialog);
        }
    }

    public <T extends GuiScreen> void update(T gui) {
        if (this.dialogMap.containsKey(gui)) {
            List<Dialog<T>> dialogList = (List<Dialog<T>>) ((List<?>) this.dialogMap.get(gui));
            for (Dialog<T> dialog : dialogList) {
                dialog.update();
            }
        }
    }

    public <T extends GuiScreen> boolean render(T gui, float mouseX, float mouseY, float partialTicks) {
        boolean flag = false;
        if (this.dialogMap.containsKey(gui)) {
            List<Dialog<T>> dialogList = (List<Dialog<T>>) ((List<?>) this.dialogMap.get(gui));
            for (Dialog<T> dialog : dialogList) {
                dialog.render(mouseX, mouseY, partialTicks);
                if (mouseX < dialog.posX || mouseX > dialog.posX + dialog.width || mouseY < dialog.posY || mouseY > dialog.posY + dialog.height) {
                    flag = true;
                }
            }
        }
        return flag;
    }

    public <T extends GuiScreen> boolean mouseClicked(T gui, float mouseX, float mouseY, int button) {
        boolean flag = false;
        if (this.dialogMap.containsKey(gui)) {
            List<Dialog<T>> dialogList = (List<Dialog<T>>) ((List<?>) this.dialogMap.get(gui));
            for (Dialog<T> dialog : Lists.reverse(dialogList)) {
                if (dialog.mouseClicked(mouseX, mouseY, button)) {
                    if (this.dialogMap.get(gui).remove(dialog)) {
                        this.dialogMap.get(gui).add(dialog);
                    }
                    return true;
                } else if (mouseX > dialog.posX && mouseX < dialog.posX + dialog.width && mouseY > dialog.posY && mouseY < dialog.posY + dialog.height) {
                    flag = true;
                }
            }
        }
        return flag;
    }

    public <T extends GuiScreen> boolean mouseDragged(T gui, float mouseX, float mouseY, int button, long timeSinceClick) {
        boolean flag = false;
        if (this.dialogMap.containsKey(gui)) {
            List<Dialog<T>> dialogList = (List<Dialog<T>>) ((List<?>) this.dialogMap.get(gui));
            for (Dialog<T> dialog : Lists.reverse(dialogList)) {
                if (dialog.mouseDragged(mouseX, mouseY, button, timeSinceClick)) {
                    return true;
                } else if (mouseX > dialog.posX && mouseX < dialog.posX + dialog.width && mouseY > dialog.posY && mouseY < dialog.posY + dialog.height) {
                    flag = true;
                }
            }
        }
        return flag;
    }

    public <T extends GuiScreen> boolean mouseReleased(T gui, float mouseX, float mouseY, int button) {
        boolean flag = false;
        if (this.dialogMap.containsKey(gui)) {
            List<Dialog<T>> dialogList = (List<Dialog<T>>) ((List<?>) this.dialogMap.get(gui));
            for (Dialog<T> dialog : Lists.reverse(dialogList)) {
                if (dialog.mouseReleased(mouseX, mouseY, button)) {
                    return true;
                } else if (mouseX > dialog.posX && mouseX < dialog.posX + dialog.width && mouseY > dialog.posY && mouseY < dialog.posY + dialog.height) {
                    flag = true;
                }
            }
        }
        return flag;
    }

    public <T extends GuiScreen> boolean keyPressed(T gui, char character, int key) {
        if (this.dialogMap.containsKey(gui)) {
            List<Dialog<T>> dialogList = (List<Dialog<T>>) ((List<?>) this.dialogMap.get(gui));
            for (Dialog<T> dialog : Lists.reverse(dialogList)) {
                if (dialog.keyPressed(character, key)) {
                    return true;
                }
            }
        }
        return false;
    }
}
