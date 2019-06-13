package de.hhu.bsinfo.neutrino.api.module;

public abstract class Module<T extends ModuleOptions> {

    private T options;

    protected abstract void onInit();

    protected abstract void onShutdown();

    @SuppressWarnings("unchecked")
    void setOptions(ModuleOptions options) {
        this.options = (T) options;
    }

    protected T getOptions() {
        return options;
    }
}
