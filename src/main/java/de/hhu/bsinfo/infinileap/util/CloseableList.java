package de.hhu.bsinfo.infinileap.util;

import java.io.Closeable;
import java.util.List;

public interface CloseableList<T extends AutoCloseable> extends List<T>, Closeable {}
