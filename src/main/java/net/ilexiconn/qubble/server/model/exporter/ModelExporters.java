package net.ilexiconn.qubble.server.model.exporter;

public enum ModelExporters {
    JAVA(new JavaExporter()),
    JAVASCRIPT(new JavaScriptExporter()),
    KOTLIN(new KotlinExporter()),
    OBJ(new OBJExporter()),
    SCALA(new ScalaExporter()),
    TABULA(new TabulaExporter());

    public static final ModelExporters[] VALUES = values();
    private IModelExporter<?> modelExporter;

    ModelExporters(IModelExporter<?> modelExporter) {
        this.modelExporter = modelExporter;
    }

    public static IModelExporter<?> getBuiltinExporter(String name) {
        for (ModelExporters exporter : ModelExporters.VALUES) {
            if (exporter.getModelExporter().getName().equals(name)) {
                return exporter.getModelExporter();
            }
        }
        return null;
    }

    public IModelExporter<?> getModelExporter() {
        return modelExporter;
    }
}
