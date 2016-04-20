package net.ilexiconn.qubble.client.gui.element;

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
        super(gui, gui.width - 120, 20, 120, gui.height - 20);
    }

    @Override
    public void render(float mouseX, float mouseY, float partialTicks) {
        this.getGUI().drawRectangle(this.getPosX(), this.getPosY(), this.getWidth(), this.getHeight(), Qubble.CONFIG.getPrimaryColor());
        this.getGUI().drawRectangle(this.getPosX() - 2, this.getPosY(), 2, this.getHeight(), Qubble.CONFIG.getAccentColor());
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
        return true;
    }

    @Override
    public boolean mouseDragged(float mouseX, float mouseY, int button, long timeSinceClick) {
        for (Element<QubbleGUI> element : this.elementList) {
            if (element.mouseDragged(mouseX, mouseY, button, timeSinceClick)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(float mouseX, float mouseY, int button) {
        for (Element<QubbleGUI> element : this.elementList) {
            if (element.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
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

    public void initModelView() {
        this.elementList.clear();
        this.addElement(new TextElement(this.getGUI(), "Selected cube", 2, 10));
        this.addElement(new InputElement(this.getGUI(), "", 2, 19, 116));
        this.addElement(new TextElement(this.getGUI(), "Dimensions", 2, 44));
        this.addElement(new SliderElement(this.getGUI(), 2, 53, true, value -> true));
        this.addElement(new SliderElement(this.getGUI(), 41, 53, true, value -> true));
        this.addElement(new SliderElement(this.getGUI(), 80, 53, true, value -> true));
        this.addElement(new TextElement(this.getGUI(), "Position", 2, 69));
        this.addElement(new SliderElement(this.getGUI(), 2, 78, value -> true));
        this.addElement(new SliderElement(this.getGUI(), 41, 78, value -> true));
        this.addElement(new SliderElement(this.getGUI(), 80, 78, value -> true));
        this.addElement(new TextElement(this.getGUI(), "Offset", 2, 94));
        this.addElement(new SliderElement(this.getGUI(), 2, 103, value -> true));
        this.addElement(new SliderElement(this.getGUI(), 41, 103, value -> true));
        this.addElement(new SliderElement(this.getGUI(), 80, 103, value -> true));
        this.addElement(new TextElement(this.getGUI(), "Scale", 2, 119));
        this.addElement(new SliderElement(this.getGUI(), 2, 128, value -> true));
        this.addElement(new SliderElement(this.getGUI(), 41, 128, value -> true));
        this.addElement(new SliderElement(this.getGUI(), 80, 128, value -> true));
        this.addElement(new TextElement(this.getGUI(), "Rotation", 2, 144));
    }

    public void initTextureView() {
        this.elementList.clear();
    }

    public void initAnimateView() {
        this.elementList.clear();
    }
}
