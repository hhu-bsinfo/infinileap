package de.hhu.bsinfo.neutrino.api.util.service;

public abstract class Service<T extends ServiceOptions> {

    private T options;

    protected abstract void onInit();

    protected abstract void onShutdown();

    @SuppressWarnings("unchecked")
    void setOptions(ServiceOptions options) {
        this.options = (T) options;
    }

    protected T getOptions() {
        return options;
    }
}
