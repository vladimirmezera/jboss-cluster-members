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

    private static final String WILDFLY_URL = "service:jmx:remote+http://localhost:9990";
    private static final String JBOSS_URL = "service:jmx:remoting-jmx://localhost:9999";
    private static final String WILDFLY_CHANNEL = "ee";
    private static final String WILDFLY_JMX_URL = "jgroups:type=channel,cluster=\"" + WILDFLY_CHANNEL + "\"";
    private static final String JBOSS_CHANNEL = "web";
    private static final String JBOSS_JMX_URL = "jgroups:type=channel,cluster=\"" + JBOSS_CHANNEL + "\"";
    private static final String JMX_CLUSTER_ATTR = "view";
    private static final String WIDLFLY_MANAGED = "wildfly-managed";
    private static final String CONTAINER = System.getProperty("arquillian.launch", WIDLFLY_MANAGED);

    @Deployment
    public static WebArchive createJBossTestArchive() {
        boolean wildfly = WIDLFLY_MANAGED.equals(CONTAINER);
        String webDescriptor;
        if (wildfly) {
            webDescriptor = "web.xml";
        } else {
            webDescriptor = "web-jboss.xml";
        }
        return ShrinkWrap.create(WebArchive.class, "test-jboss.war").addClass(JmxCluster.class)
                .setWebXML(webDescriptor);
    }

    @RunAsClient
    @Test
    public void testConnect() {
        try {

            RemotingConnectorProvider s = new RemotingConnectorProvider();
            JMXServiceURL serviceUrl;
            boolean wildfly = WIDLFLY_MANAGED.equals(CONTAINER);
            if (wildfly) {
                serviceUrl = new JMXServiceURL(WILDFLY_URL);
            } else {
                serviceUrl = new JMXServiceURL(JBOSS_URL);
            }
            Map<String, Object> env = new HashMap<>();
            JMXConnector connector = s.newJMXConnector(serviceUrl, env);
            connector.connect();
            MBeanServerConnection mbeanConn = connector.getMBeanServerConnection();
            Assert.assertTrue(mbeanConn.getMBeanCount() > 0);
            Thread.sleep(3000);
            Object attr;
            if (wildfly) {
                attr = mbeanConn.getAttribute(ObjectName.getInstance(WILDFLY_JMX_URL), JMX_CLUSTER_ATTR);
            } else {
                attr = mbeanConn.getAttribute(ObjectName.getInstance(JBOSS_JMX_URL), "View");
            }
            Assert.assertNotNull(attr);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        Assert.assertTrue(true);
    }
}
