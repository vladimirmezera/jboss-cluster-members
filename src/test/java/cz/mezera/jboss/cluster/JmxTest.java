package cz.mezera.jboss.cluster;

import cz.meza.jboss.cluster.JmxCluster;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.remotingjmx.RemotingConnectorProvider;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;
import java.util.HashMap;
import java.util.Map;

@RunWith(Arquillian.class)
public class JmxTest {

    private static final String URL = "service:jmx:http-remoting-jmx://localhost:9990";
    private static final String CHANNEL = "ee";
    private static final String WILDFLY_JMX_URL = "jgroups:type=channel,cluster=\"" + CHANNEL + "\"";
    private static final String WIDLFY_JMX_CLUSTER_ATTR = "view";

    @Deployment
    public static WebArchive createTestArchive() {
        return ShrinkWrap.create(WebArchive.class, "test-1.war").addClass(JmxCluster.class)
                .setWebXML("web.xml");
    }


    @RunAsClient
    @Test
    public void testConnect() {
        try {
            RemotingConnectorProvider s = new RemotingConnectorProvider();
            JMXServiceURL serviceUrl = new JMXServiceURL(URL);
            Map<String, Object> env = new HashMap<>();
            JMXConnector connector = s.newJMXConnector(serviceUrl, env);
            connector.connect();
            MBeanServerConnection mbeanConn = connector.getMBeanServerConnection();
            Assert.assertTrue(mbeanConn.getMBeanCount() > 0);
            Thread.sleep(3000);
            Object attr = mbeanConn.getAttribute(ObjectName.getInstance(WILDFLY_JMX_URL), WIDLFY_JMX_CLUSTER_ATTR);
            Assert.assertNotNull(attr);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        Assert.assertTrue(true);

    }
}
