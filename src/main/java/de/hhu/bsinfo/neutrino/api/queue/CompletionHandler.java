package de.hhu.bsinfo.neutrino.api.queue;

import de.hhu.bsinfo.neutrino.verbs.WorkCompletion;

public interface CompletionHandler {
    void onComplete(long id);
    void onError(long id, WorkCompletion.Status status);
}
