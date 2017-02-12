package net.ilexiconn.qubble.client.model.exporter;

public enum ModelExporters {
    JAVA(new JavaExporter()),
    JAVASCRIPT(new JavaScriptExporter()),
    KOTLIN(new KotlinExporter()),
    OBJ(new OBJExporter()),
    SCALA(new ScalaExporter()),
    TABULA(new TabulaExporter()),
    TEXTURE_MAP(new TextureMapExporter()),
    BLOCK_JSON(new JsonExporter());

    public static final ModelExporters[] VALUES = values();
    public static final IModelExporter<?, ?, ?>[] EXPORTERS;

    static {
        EXPORTERS = new IModelExporter<?, ?, ?>[ModelExporters.VALUES.length];
        for (int i = 0; i < ModelExporters.EXPORTERS.length; i++) {
            ModelExporters.EXPORTERS[i] = ModelExporters.VALUES[i].getModelExporter();
        }
    }

    private IModelExporter<?, ?, ?> modelExporter;

    ModelExporters(IModelExporter<?, ?, ?> modelExporter) {
        this.modelExporter = modelExporter;
    }

    public static IModelExporter<?, ?, ?> getBuiltinExporter(String name) {
        for (ModelExporters exporter : ModelExporters.VALUES) {
            if (exporter.getModelExporter().getName().equals(name)) {
                return exporter.getModelExporter();
            }
        }
        return null;
    }

    public IModelExporter<?, ?, ?> getModelExporter() {
        return this.modelExporter;
    }
}
