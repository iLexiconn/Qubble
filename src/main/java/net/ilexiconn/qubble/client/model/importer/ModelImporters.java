package net.ilexiconn.qubble.client.model.importer;

public enum ModelImporters {
    JAVASCRIPT(new JavaScriptImporter()),
    TABULA(new TabulaImporter()),
    TECHNE(new TechneImporter()),
    BLOCK_JSON(new JsonImporter());

    public static final ModelImporters[] VALUES = values();
    public static final IModelImporter<?, ?, ?>[] IMPORTERS;

    static {
        IMPORTERS = new IModelImporter<?, ?, ?>[ModelImporters.VALUES.length];
        for (int i = 0; i < ModelImporters.IMPORTERS.length; i++) {
            ModelImporters.IMPORTERS[i] = ModelImporters.VALUES[i].getModelImporter();
        }
    }

    private IModelImporter<?, ?, ?> modelImporter;

    ModelImporters(IModelImporter<?, ?, ?> modelImporter) {
        this.modelImporter = modelImporter;
    }

    public static IModelImporter<?, ?, ?> getBuiltinImporter(String name) {
        for (ModelImporters importer : ModelImporters.VALUES) {
            if (importer.getModelImporter().getName().equals(name)) {
                return importer.getModelImporter();
            }
        }
        return null;
    }

    public IModelImporter<?, ?, ?> getModelImporter() {
        return this.modelImporter;
    }
}
