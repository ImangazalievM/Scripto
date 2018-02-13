package imangazaliev.scripto.java;

public class JavaInterfaceConfig {

    private boolean annotationProtectionEnabled;

    public JavaInterfaceConfig() {
        annotationProtectionEnabled = false;
    }

    public JavaInterfaceConfig enableAnnotationProtection(boolean annotationProtectionEnabled) {
        this.annotationProtectionEnabled = annotationProtectionEnabled;
        return this;
    }

    public boolean isAnnotationProtectionEnabled() {
        return annotationProtectionEnabled;
    }

}
