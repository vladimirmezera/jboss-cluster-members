package cz.mezera.jboss.cluster;

import cz.meza.jboss.cluster.JmxCluster;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.remotingjmx.RemotingConnectorProvider;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;
import java.util.HashMap;
import java.util.Map;

@RunWith(Arquillian.class)
public class JmxClusterTest {

    @Deployment
    public static WebArchive createJavaTestArchive() {
        return ShrinkWrap.create(WebArchive.class, "test-1.war").addAsWebInfResource("web.xml", "web.xml");
    }

    @RunAsClient
    @Test
    public void testConnect() throws Exception{
        String url = "service:jmx:http-remoting-jmx://localhost:9990";

        Thread.sleep(30000);

        RemotingConnectorProvider s = new RemotingConnectorProvider();
        JMXServiceURL serviceUrl = new JMXServiceURL(url);
        Map<String, Object> env = new HashMap<>();
        JMXConnector connector = s.newJMXConnector(serviceUrl, env);
        connector.connect();
        MBeanServerConnection mbeanConn = connector.getMBeanServerConnection();

       // mbeanConn.getAttribute(ObjectName.getInstance("jgroups:type=channel,cluster=\"web\""), "View");


    }
}
