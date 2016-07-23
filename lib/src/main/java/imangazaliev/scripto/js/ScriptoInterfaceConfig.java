package imangazaliev.scripto.js;

public class ScriptoInterfaceConfig {

    private boolean annotationProtectionEnabled;

    public ScriptoInterfaceConfig() {
        annotationProtectionEnabled = false;
    }

    public void enableAnnotationProtection(boolean annotationProtectionEnabled) {
        this.annotationProtectionEnabled = annotationProtectionEnabled;
    }

    public boolean isAnnotationProtectionEnabled() {
        return annotationProtectionEnabled;
    }

}
