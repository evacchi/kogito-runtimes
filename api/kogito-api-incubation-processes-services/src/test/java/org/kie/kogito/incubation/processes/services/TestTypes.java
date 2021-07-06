package org.kie.kogito.incubation.processes.services;

import org.kie.kogito.incubation.common.DataContext;
import org.kie.kogito.incubation.common.DefaultCastable;
import org.kie.kogito.incubation.common.MapLikeDataContext;
import org.kie.kogito.incubation.common.MetaDataContext;
import org.kie.kogito.incubation.processes.ProcessId;

public class TestTypes {
    static class MyDataContext implements DataContext, DefaultCastable {}
    public void straightThroughProcesses() {
        // let's just make the compiler happy
        StraightThroughProcessService svc = null;
        MapLikeDataContext ctx = null;
        ProcessId someProcessId = null;

        // set a context using a Map-like interface
        ctx.set("some-param", 1);

        // metadata can be set as well (it will go in a different map)
        MetaDataContext metactx = ctx.as(MetaDataContext.class);
        metactx.set("some-metadata", "xyz");

        // evaluate the process
        DataContext result =
                svc.evaluate(someProcessId, ctx);

        // bind the data in the result to a typed bean
        MyDataContext mdc = result.as(MyDataContext.class);

    }
}
