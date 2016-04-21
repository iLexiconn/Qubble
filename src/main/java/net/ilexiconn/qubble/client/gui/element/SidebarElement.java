package net.ilexiconn.qubble.client.gui.element;

import net.ilexiconn.llibrary.client.model.qubble.QubbleCube;
import net.ilexiconn.qubble.Qubble;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class SidebarElement extends Element<QubbleGUI> {
    private List<Element<QubbleGUI>> elementList = new ArrayList<>();

    public SidebarElement(QubbleGUI gui) {
        super(gui, gui.width - 122, 20, 122, gui.height - 20);
    }

    @Override
    public void render(float mouseX, float mouseY, float partialTicks) {
        this.getGUI().drawRectangle(this.getPosX(), this.getPosY(), this.getWidth(), this.getHeight(), Qubble.CONFIG.getPrimaryColor());
        this.getGUI().drawRectangle(this.getPosX(), this.getPosY(), 2, this.getHeight(), Qubble.CONFIG.getAccentColor());
        for (Element<QubbleGUI> element : this.elementList) {
            element.render(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, int button) {
        for (Element<QubbleGUI> element : this.elementList) {
            if (element.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(float mouseX, float mouseY, int button, long timeSinceClick) {
        for (Element<QubbleGUI> element : this.elementList) {
            if (element.mouseDragged(mouseX, mouseY, button, timeSinceClick)) {
                return true;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, timeSinceClick);
    }

    @Override
    public boolean mouseReleased(float mouseX, float mouseY, int button) {
        for (Element<QubbleGUI> element : this.elementList) {
            if (element.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(char character, int keyCode) {
        for (Element<QubbleGUI> element : this.elementList) {
            if (element.keyPressed(character, keyCode)) {
                return true;
            }
        }
        return false;
    }

    public void addElement(Element<QubbleGUI> element) {
        this.elementList.add(element.withParent(this));
    }

    public <T extends Element<QubbleGUI>> T getElement(Class<T> type, int index) {
        int currentIndex = 0;
        for (Element<QubbleGUI> element : this.elementList) {
            if (type.isAssignableFrom(element.getClass())) {
                if (currentIndex == index) {
                    return (T) element;
                } else {
                    currentIndex++;
                }
            }
        }
        return null;
    }

    public void populateFields(QubbleCube cube) {
        switch (this.getGUI().getMode()) {
            case MODEL: {
                this.getElement(InputElement.class, 0).clearText();
                this.getElement(InputElement.class, 0).writeText(cube.getName());
                this.getElement(InputElement.class, 0).setEditable(true);
                this.getElement(SliderElement.class, 0).setValue(cube.getDimensionX());
                this.getElement(SliderElement.class, 0).setEditable(true);
                this.getElement(SliderElement.class, 1).setValue(cube.getDimensionY());
                this.getElement(SliderElement.class, 1).setEditable(true);
                this.getElement(SliderElement.class, 2).setValue(cube.getDimensionZ());
                this.getElement(SliderElement.class, 2).setEditable(true);
                this.getElement(SliderElement.class, 3).setValue(cube.getPositionX());
                this.getElement(SliderElement.class, 3).setEditable(true);
                this.getElement(SliderElement.class, 4).setValue(cube.getPositionY());
                this.getElement(SliderElement.class, 4).setEditable(true);
                this.getElement(SliderElement.class, 5).setValue(cube.getPositionZ());
                this.getElement(SliderElement.class, 5).setEditable(true);
                this.getElement(SliderElement.class, 6).setValue(cube.getOffsetX());
                this.getElement(SliderElement.class, 6).setEditable(true);
                this.getElement(SliderElement.class, 7).setValue(cube.getOffsetY());
                this.getElement(SliderElement.class, 7).setEditable(true);
                this.getElement(SliderElement.class, 8).setValue(cube.getOffsetZ());
                this.getElement(SliderElement.class, 8).setEditable(true);
                this.getElement(SliderElement.class, 9).setValue(cube.getScaleX());
                this.getElement(SliderElement.class, 9).setEditable(true);
                this.getElement(SliderElement.class, 10).setValue(cube.getScaleY());
                this.getElement(SliderElement.class, 10).setEditable(true);
                this.getElement(SliderElement.class, 11).setValue(cube.getScaleZ());
                this.getElement(SliderElement.class, 11).setEditable(true);
                break;
            }
            case TEXTURE: {
                break;
            }
            case ANIMATE: {
                break;
            }
        }
    }

    public void clearFields() {
        switch (this.getGUI().getMode()) {
            case MODEL: {
                this.getElement(InputElement.class, 0).clearText();
                this.getElement(InputElement.class, 0).setEditable(false);
                for (int i = 0; i < 12; i++) {
                    this.getElement(SliderElement.class, i).setValue(0.0F);
                    this.getElement(SliderElement.class, i).setEditable(false);
                }
                break;
            }
            case TEXTURE: {
                break;
            }
            case ANIMATE: {
                break;
            }
        }
    }

    public void initModelView() {
        this.elementList.clear();
        this.addElement(new TextElement(this.getGUI(), "Selected cube", 4, 10));
        this.addElement(new InputElement(this.getGUI(), "", 4, 19, 116));
        this.addElement(new TextElement(this.getGUI(), "Dimensions", 4, 44));
        this.addElement(new SliderElement(this.getGUI(), 4, 53, true, value -> true));
        this.addElement(new SliderElement(this.getGUI(), 43, 53, true, value -> true));
        this.addElement(new SliderElement(this.getGUI(), 82, 53, true, value -> true));
        this.addElement(new TextElement(this.getGUI(), "Position", 4, 69));
        this.addElement(new SliderElement(this.getGUI(), 4, 78, value -> true));
        this.addElement(new SliderElement(this.getGUI(), 43, 78, value -> true));
        this.addElement(new SliderElement(this.getGUI(), 82, 78, value -> true));
        this.addElement(new TextElement(this.getGUI(), "Offset", 4, 94));
        this.addElement(new SliderElement(this.getGUI(), 4, 103, value -> true));
        this.addElement(new SliderElement(this.getGUI(), 43, 103, value -> true));
        this.addElement(new SliderElement(this.getGUI(), 82, 103, value -> true));
        this.addElement(new TextElement(this.getGUI(), "Scale", 4, 119));
        this.addElement(new SliderElement(this.getGUI(), 4, 128, value -> true));
        this.addElement(new SliderElement(this.getGUI(), 43, 128, value -> true));
        this.addElement(new SliderElement(this.getGUI(), 82, 128, value -> true));
        this.addElement(new TextElement(this.getGUI(), "Rotation", 4, 144));
    }

    public void initTextureView() {
        this.elementList.clear();
    }

    public void initAnimateView() {
        this.elementList.clear();
    }
}
