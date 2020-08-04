package de.hhu.bsinfo.neutrino.verbs.panama.util;

import java.io.Closeable;
import java.util.List;

public interface CloseableList<T extends AutoCloseable> extends List<T>, Closeable {}
