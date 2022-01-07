package si.fri.rso.uniborrow.items.api.v1;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import com.kumuluz.ee.discovery.annotations.RegisterService;

@RegisterService(value = "uniborrow-items-service", environment = "dev", version = "1.0.0")
@ApplicationPath("/v1")
public class ItemsApplication extends Application {

}
