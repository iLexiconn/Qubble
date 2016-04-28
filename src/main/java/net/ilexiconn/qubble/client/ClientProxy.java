package net.ilexiconn.qubble.client;

import net.ilexiconn.llibrary.client.model.qubble.QubbleCube;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;
import net.ilexiconn.qubble.client.world.DummyWorld;
import net.ilexiconn.qubble.server.ServerProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@SideOnly(Side.CLIENT)
public class ClientProxy extends ServerProxy {
    public static final int QUBBLE_BUTTON_ID = "QUBBLE_BUTTON_ID".hashCode();
    public static final Minecraft MINECRAFT = Minecraft.getMinecraft();
    public static final File QUBBLE_DIRECTORY = new File(".", "llibrary" + File.separator + "qubble");
    public static final File QUBBLE_MODEL_DIRECTORY = new File(QUBBLE_DIRECTORY, "models");
    public static final File QUBBLE_TEXTURE_DIRECTORY = new File(QUBBLE_DIRECTORY, "textures");
    public static final File QUBBLE_EXPORT_DIRECTORY = new File(QUBBLE_DIRECTORY, "exports");
    public static final Map<String, QubbleModel> GAME_MODELS = new HashMap<>();
    public static final Map<String, ResourceLocation> GAME_TEXTURES = new HashMap<>();
    private static Field TEXTURE_QUADS_FIELD;

    @Override
    public void onPreInit() {
        super.onPreInit();
        MinecraftForge.EVENT_BUS.register(ClientEventHandler.INSTANCE);
        if (!QUBBLE_MODEL_DIRECTORY.exists()) {
            QUBBLE_MODEL_DIRECTORY.mkdirs();
        }
        if (!QUBBLE_TEXTURE_DIRECTORY.exists()) {
            QUBBLE_TEXTURE_DIRECTORY.mkdirs();
        }
        if (!QUBBLE_EXPORT_DIRECTORY.exists()) {
            QUBBLE_EXPORT_DIRECTORY.mkdirs();
        }
        for (Field field : ModelBox.class.getDeclaredFields()) {
            if (field.getType() == TexturedQuad[].class) {
                field.setAccessible(true);
                TEXTURE_QUADS_FIELD = field;
                break;
            }
        }
    }

    @Override
    public void onInit() {
        super.onInit();
    }

    @Override
    public void onPostInit() {
        super.onPostInit();
        for (Map.Entry<Class<? extends Entity>, Render<? extends Entity>> entry : MINECRAFT.getRenderManager().entityRenderMap.entrySet()) {
            Render<? extends Entity> renderer = entry.getValue();
            String entityName = entry.getKey().getSimpleName().replaceAll("Entity", "");
            for (Field field : this.getAllFields(renderer.getClass())) {
                try {
                    if (ModelBase.class.isAssignableFrom(field.getType())) {
                        field.setAccessible(true);
                        QubbleModel model = this.parseModel((ModelBase) field.get(renderer), entry.getKey());
                        if (model.getCubes().size() > 0) {
                            GAME_MODELS.put(entityName, model);
                        }
                    } else if (ResourceLocation[].class.isAssignableFrom(field.getType()) && !GAME_TEXTURES.containsKey(entityName)) {
                        field.setAccessible(true);
                        ResourceLocation[] textures = (ResourceLocation[]) field.get(renderer);
                        if (textures.length > 0) {
                            ResourceLocation texture = textures[0];
                            if (!texture.toString().contains("shadow")) {
                                GAME_TEXTURES.put(entityName, texture);
                            }
                        }
                    } else if (ResourceLocation.class.isAssignableFrom(field.getType()) && !GAME_TEXTURES.containsKey(entityName)) {
                        field.setAccessible(true);
                        ResourceLocation texture = (ResourceLocation) field.get(renderer);
                        if (!texture.toString().contains("shadow")) {
                            GAME_TEXTURES.put(entityName, texture);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Failed to load model from " + renderer.getClass() + "#" + field.getName());
                    e.printStackTrace();
                }
            }
        }
        for (Map.Entry<Class<? extends TileEntity>, TileEntitySpecialRenderer<? extends TileEntity>> entry : TileEntityRendererDispatcher.instance.mapSpecialRenderers.entrySet()) {
            TileEntitySpecialRenderer<? extends TileEntity> renderer = entry.getValue();
            String tileName = entry.getKey().getSimpleName();
            for (Field field : this.getAllFields(renderer.getClass())) {
                try {
                    if (ModelBase.class.isAssignableFrom(field.getType())) {
                        field.setAccessible(true);
                        QubbleModel model = this.parseModel((ModelBase) field.get(renderer), entry.getKey());
                        if (model.getCubes().size() > 0) {
                            GAME_MODELS.put(tileName, model);
                        }
                    } else if (ResourceLocation[].class.isAssignableFrom(field.getType()) && !GAME_TEXTURES.containsKey(tileName)) {
                        field.setAccessible(true);
                        ResourceLocation[] textures = (ResourceLocation[]) field.get(renderer);
                        if (textures.length > 0) {
                            ResourceLocation texture = textures[0];
                            if (!texture.toString().contains("destroy_stage")) {
                                GAME_TEXTURES.put(tileName, texture);
                            }
                        }
                    } else if (ResourceLocation.class.isAssignableFrom(field.getType()) && !GAME_TEXTURES.containsKey(tileName)) {
                        field.setAccessible(true);
                        ResourceLocation texture = (ResourceLocation) field.get(renderer);
                        if (!texture.toString().contains("destroy_stage")) {
                            GAME_TEXTURES.put(tileName, texture);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Failed to load model from " + renderer.getClass() + "#" + field.getName());
                    e.printStackTrace();
                }
            }
        }
    }

    private List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new LinkedList<>();
        Collections.addAll(fields, clazz.getDeclaredFields());
        if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
            fields.addAll(this.getAllFields(clazz.getSuperclass()));
        }
        return fields;
    }

    private QubbleModel parseModel(ModelBase model, Class<?> clazz) {
        QubbleModel qubbleModel = QubbleModel.create(model.getClass().getSimpleName().replaceAll("Model", ""), "Unknown", model.textureWidth, model.textureHeight);
        if (clazz != null && Entity.class.isAssignableFrom(clazz)) {
            try {
                Entity entity = (Entity) clazz.getConstructor(World.class).newInstance(new DummyWorld());
                try {
                    model.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, entity);
                } catch (Exception e) {
                }
                if (entity instanceof EntityLivingBase) {
                    try {
                        model.setLivingAnimations((EntityLivingBase) entity, 0.0F, 0.0F, 0.0F);
                    } catch (Exception e) {
                    }
                }
                try {
                    model.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
                } catch (Exception e) {
                }
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            }
        }
        Map<String, ModelRenderer> cuboidsWithNames = this.getCuboidsWithNames(model);
        for (Map.Entry<String, ModelRenderer> entry : cuboidsWithNames.entrySet()) {
            ModelRenderer modelRenderer = entry.getValue();
            if (modelRenderer != null && modelRenderer.cubeList != null) {
                qubbleModel.setTextureWidth((int) modelRenderer.textureWidth);
                qubbleModel.setTextureHeight((int) modelRenderer.textureHeight);
                break;
            }
        }
        for (Map.Entry<String, ModelRenderer> entry : cuboidsWithNames.entrySet()) {
            qubbleModel.getCubes().addAll(parseModelRenderer(model, qubbleModel, entry.getKey(), entry.getValue(), null));
        }
        return qubbleModel;
    }

    private List<QubbleCube> parseModelRenderer(ModelBase model, QubbleModel qubbleModel, String name, ModelRenderer modelRenderer, QubbleCube parent) {
        List<QubbleCube> cubes = new ArrayList<>();
        int boxIndex = 0;
        if (modelRenderer != null && modelRenderer.cubeList != null) {
            for (ModelBox box : modelRenderer.cubeList) {
                float textureWidth = qubbleModel.getTextureWidth();
                float textureHeight = qubbleModel.getTextureHeight();
                if (modelRenderer.textureWidth != 64 || modelRenderer.textureHeight != 32) {
                    textureWidth = modelRenderer.textureWidth;
                    textureHeight = modelRenderer.textureHeight;
                }
                QubbleCube cube = QubbleCube.create((modelRenderer.boxName != null ? modelRenderer.boxName : name) + (boxIndex != 0 ? box.boxName != null ? box.boxName : "_" + boxIndex : ""));
                cube.setPosition(modelRenderer.rotationPointX, modelRenderer.rotationPointY, modelRenderer.rotationPointZ);
                cube.setRotation((float) Math.toDegrees(modelRenderer.rotateAngleX), (float) Math.toDegrees(modelRenderer.rotateAngleY), (float) Math.toDegrees(modelRenderer.rotateAngleZ));
                cube.setOffset(box.posX1, box.posY1, box.posZ1);
                cube.setDimensions((int) Math.abs(box.posX2 - box.posX1), (int) Math.abs(box.posY2 - box.posY1), (int) Math.abs(box.posZ2 - box.posZ1));
                TextureOffset textureOffset = model.getTextureOffset(box.boxName);
                if (textureOffset != null) {
                    cube.setTexture(textureOffset.textureOffsetX, textureOffset.textureOffsetY);
                } else {
                    TexturedQuad[] quads = this.getTexturedQuads(box);
                    if (quads != null) {
                        PositionTextureVertex[] vertices = quads[1].vertexPositions;
                        cube.setTextureMirrored((vertices[2].vector3D.yCoord - vertices[0].vector3D.yCoord - cube.getDimensionY()) / 2.0F < 0.0F);
                        if (vertices[cube.isTextureMirrored() ? 2 : 1].texturePositionY > vertices[cube.isTextureMirrored() ? 1 : 2].texturePositionY) {
                            cube.setTextureMirrored(!cube.isTextureMirrored());
                        }
                        cube.setTexture((int) (vertices[cube.isTextureMirrored() ? 2 : 1].texturePositionX * textureWidth), (int) ((vertices[cube.isTextureMirrored() ? 2 : 1].texturePositionY * textureHeight) - cube.getDimensionZ()));
                    }
                }
                boxIndex++;
                cubes.add(cube);
            }
            if (cubes.size() > 0 && modelRenderer.childModels != null) {
                int i = 0;
                for (ModelRenderer child : modelRenderer.childModels) {
                    this.parseModelRenderer(model, qubbleModel, child.boxName != null ? child.boxName : name + "_" + i, child, cubes.get(0));
                    i++;
                }
            }
        }
        if (parent != null) {
            parent.getChildren().addAll(cubes);
        }
        return cubes;
    }

    private Map<String, ModelRenderer> getCuboidsWithNames(ModelBase model) {
        Map<String, ModelRenderer> cuboids = new HashMap<>();
        for (Field field : this.getAllFields(model.getClass())) {
            try {
                if (ModelRenderer.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    ModelRenderer modelRenderer = (ModelRenderer) field.get(model);
                    if (modelRenderer != null) {
                        cuboids.put(field.getName(), modelRenderer);
                    }
                } else if (ModelRenderer[].class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    ModelRenderer[] boxes = (ModelRenderer[]) field.get(model);
                    if (boxes != null) {
                        for (int i = 0; i < boxes.length; i++) {
                            cuboids.put(field.getName() + "_" + i, boxes[i]);
                        }
                    }
                } else if (List.class.isAssignableFrom(field.getType())) {
                    if (field.getDeclaringClass() != ModelBase.class) {
                        field.setAccessible(true);
                        List boxes = (List) field.get(model);
                        if (boxes != null) {
                            for (int i = 0; i < boxes.size(); i++) {
                                Object obj = boxes.get(i);
                                if (obj instanceof ModelRenderer) {
                                    cuboids.put(field.getName() + "_" + i, (ModelRenderer) obj);
                                }
                            }
                        }
                    }
                }
            } catch (IllegalAccessException e) {
            }
        }
        return cuboids;
    }

    private TexturedQuad[] getTexturedQuads(ModelBox box) {
        try {
            return (TexturedQuad[]) TEXTURE_QUADS_FIELD.get(box);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> getGameModels() {
        List<String> gameModels = new LinkedList<>();
        for (Map.Entry<String, QubbleModel> entry : GAME_MODELS.entrySet()) {
            gameModels.add(entry.getKey());
        }
        Collections.sort(gameModels);
        return gameModels;
    }
}
