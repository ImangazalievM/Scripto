package imangazaliev.scripto.js;

public class ScriptoInterfaceConfig {

    private boolean annotationProtectionEnabled;

    public ScriptoInterfaceConfig() {
        annotationProtectionEnabled = false;
    }

    public ScriptoInterfaceConfig enableAnnotationProtection(boolean annotationProtectionEnabled) {
        this.annotationProtectionEnabled = annotationProtectionEnabled;
        return this;
    }

    public boolean isAnnotationProtectionEnabled() {
        return annotationProtectionEnabled;
    }

}
