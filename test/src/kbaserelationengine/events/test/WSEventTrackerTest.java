package kbaserelationengine.events.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import org.apache.http.HttpHost;
import org.junit.Before;
import org.junit.Test;

import kbaserelationengine.events.ESObjectStatusStorage;
import kbaserelationengine.events.ObjectStatus;
import kbaserelationengine.events.ObjectStatusEventListener;
import kbaserelationengine.events.ObjectStatusStorage;
import kbaserelationengine.events.WSEventTracker;
import kbaserelationengine.events.test.fake.FakeObjectStatusStorage;
import us.kbase.auth.AuthToken;
import us.kbase.common.service.JsonClientException;
import workspace.GetObjects2Params;
import workspace.GetObjects2Results;
import workspace.ObjectSpecification;
import workspace.WorkspaceClient;


public class WSEventTrackerTest {
	WSEventTracker wet;
	ObjectStatusStorage fakeStorage;
	ESObjectStatusStorage esStorage;
	
	@Before
	public void init() throws MalformedURLException{
        fakeStorage = new FakeObjectStatusStorage();
        esStorage = new ESObjectStatusStorage(new HttpHost("localhost", 9200));

        ObjectStatusStorage storage = esStorage;
        
        
        AuthToken token = new AuthToken(System.getenv().get("AUTH_TOKEN"), "unknown") ;
    	URL wsURL = new URL("https://ci.kbase.us/services/ws");
        wet = new WSEventTracker(wsURL, token , storage);
        
        // Register listeners
        ObjectStatusEventListener listener;         
        listener = new ObjectStatusEventListener(){
			@Override
			public void statusChanged(ObjectStatus obj) throws IOException {
				System.out.println(obj);
			}
        }; 
        wet.registerEventListener(listener);
        
        wet.registerEventListener(esStorage);
	}
	
    @Test
    public void testRefilQueueStorage() throws Exception {      
//		esStorage.deleteStorage();
//		esStorage.createStorage();    	
        wet.update();        
    }	
    
    
    @Test
    public void test02() throws Exception {        
    }	
    
//  @Test
    public void test99() throws IOException, JsonClientException{
    	WorkspaceClient wsClient =  wet.wsClient();
    	GetObjects2Params params = new GetObjects2Params().withObjects(Arrays.asList(
    			new ObjectSpecification().withWsid(19971L).withObjid(2L).withVer(0L)
    	));
		GetObjects2Results ret = wsClient.getObjects2(params );
		System.out.println("=======");
		
		System.out.println(ret.getData().get(0).getInfo());    	
  }
	
}
