package net.ilexiconn.qubble.client.gui.element.sidebar.block;

import net.ilexiconn.qubble.client.project.Project;
import net.ilexiconn.qubble.client.gui.QubbleGUI;
import net.ilexiconn.qubble.client.gui.element.sidebar.SidebarElement;
import net.ilexiconn.qubble.client.gui.element.sidebar.SidebarHandler;
import net.ilexiconn.qubble.client.project.ModelType;
import net.ilexiconn.qubble.client.model.wrapper.BlockCuboidWrapper;
import net.ilexiconn.qubble.client.model.wrapper.BlockModelWrapper;
import net.minecraft.util.EnumFacing;

public class BlockTextureSidebarHandler extends SidebarHandler<BlockCuboidWrapper, BlockModelWrapper> {
    @Override
    public void createProperties(QubbleGUI gui, SidebarElement sidebar) {
    }

    @Override
    public void update(QubbleGUI gui, Project project) {
    }

    @Override
    protected void initProperties(BlockModelWrapper model, BlockCuboidWrapper cuboid) {
    }

    @Override
    protected void createElements(QubbleGUI gui, SidebarElement sidebar) {
        int x = 0;
        int y = 0;

        for (EnumFacing facing : EnumFacing.VALUES) {
            float renderX = x * (BlockFaceElement.SIZE + 14) + 12;
            float renderY = y * (BlockFaceElement.SIZE + 16) + 48;

            new BlockFaceElement(this.gui, facing, renderX, renderY).withParent(sidebar);

            if (++x >= 2) {
                x = 0;
                y++;
            }
        }
    }

    @Override
    protected void initElements(BlockModelWrapper model, BlockCuboidWrapper cuboid) {
    }

    @Override
    public ModelType<BlockCuboidWrapper, BlockModelWrapper> getModelType() {
        return ModelType.BLOCK;
    }
}
