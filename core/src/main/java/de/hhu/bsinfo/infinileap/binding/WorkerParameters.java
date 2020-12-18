package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.util.NativeObject;
import org.openucx.ucx_h.ucp_worker_params_t;

public class WorkerParameters extends NativeObject {

    /* package-private */ WorkerParameters() {
        super(ucp_worker_params_t.allocate());
    }
}
