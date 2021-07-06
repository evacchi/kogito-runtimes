package org.kie.kogito.incubation.processes.services;

import org.kie.kogito.incubation.common.DataContext;
import org.kie.kogito.incubation.processes.ProcessId;

public interface StraightThroughProcessService {
    DataContext evaluate(ProcessId processId, DataContext inputContext);
}
