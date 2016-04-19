package net.ilexiconn.qubble.client;

import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.Rectangle;

@SideOnly(Side.CLIENT)
public enum ClientEventHandler {
    INSTANCE;

    @SubscribeEvent
    public void onInitGuiPost(GuiScreenEvent.InitGuiEvent.Post event) {
        GuiScreen gui = event.getGui();

        if (gui instanceof GuiMainMenu) {
            int offsetX = 0;
            int offsetY = 0;
            int baseX = gui.width / 2 + 104;
            int baseY = gui.height / 4 + 48 + 24 * 2;
            int buttonX = baseX + offsetX;
            int buttonY = baseY + offsetY;
            while (true) {
                if (buttonX < 0) {
                    if (offsetY <= -48) {
                        buttonX = 0;
                        buttonY = 0;
                        break;
                    } else {
                        offsetX = 0;
                        offsetY += 24;
                        buttonX = baseX + offsetX;
                        buttonY = baseY + offsetY;
                    }
                }

                Rectangle rectangle = new Rectangle(buttonX, buttonY, 20, 20);
                boolean intersects = false;
                for (int i = 0; i < event.getButtonList().size(); i++) {
                    GuiButton button = event.getButtonList().get(i);
                    if (!intersects) {
                        intersects = rectangle.intersects(new Rectangle(button.xPosition, button.yPosition, button.width, button.height));
                    }
                }

                if (!intersects) {
                    break;
                }

                buttonX += 24;
            }

            event.getButtonList().add(new GuiButton(ClientProxy.QUBBLE_BUTTON_ID, buttonX, buttonY, 20, 20, "Q"));
        }
    }

    @SubscribeEvent
    public void onButtonPressPre(GuiScreenEvent.ActionPerformedEvent.Pre event) {
        if (event.getGui() instanceof GuiMainMenu && event.getButton().id == ClientProxy.QUBBLE_BUTTON_ID) {
            ClientProxy.MINECRAFT.displayGuiScreen(new QubbleGUI(event.getGui()));
        }
    }
}
