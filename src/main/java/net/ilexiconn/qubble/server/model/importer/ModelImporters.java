package net.ilexiconn.qubble.server.model.importer;

public enum ModelImporters {
    JAVASCRIPT(new JavaScriptImporter()),
    TABULA(new TabulaImporter()),
    TECHNE(new TechneImporter());

    public static final ModelImporters[] VALUES = values();
    private IModelImporter<?> modelImporter;

    ModelImporters(IModelImporter<?> modelImporter) {
        this.modelImporter = modelImporter;
    }

    public static IModelImporter<?> getBuiltinImporter(String name) {
        for (ModelImporters importer : ModelImporters.VALUES) {
            if (importer.getModelImporter().getName().equals(name)) {
                return importer.getModelImporter();
            }
        }
        return null;
    }

    public IModelImporter<?> getModelImporter() {
        return modelImporter;
    }
}
