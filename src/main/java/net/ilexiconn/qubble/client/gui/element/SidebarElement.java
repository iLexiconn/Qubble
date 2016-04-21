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
