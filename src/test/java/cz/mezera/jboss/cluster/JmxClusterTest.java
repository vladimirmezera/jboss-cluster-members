package cz.mezera.jboss.cluster;

import org.jboss.remotingjmx.RemotingConnectorProvider;
import org.junit.Test;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class JmxClusterTest {

    @Test
    public void testConnect() throws Exception{
        String host = "localhost";  // or some A.B.C.D
        String url = "service:jmx:remoting-jmx://localhost:9999";

        RemotingConnectorProvider s = new RemotingConnectorProvider();
        JMXServiceURL serviceUrl = new JMXServiceURL(url);
        Map<String, Object> env = new HashMap<>();
        JMXConnector connector = s.newJMXConnector(serviceUrl, env);
        connector.connect();
        MBeanServerConnection mbeanConn = connector.getMBeanServerConnection();

        mbeanConn.getAttribute(ObjectName.getInstance("jgroups:type=channel,cluster=\"web\""), "View");


    }
}
