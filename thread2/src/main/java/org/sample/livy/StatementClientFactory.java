package org.sample.livy;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.net.URI;
import java.util.Properties;

/**
 * Created by chenxh on 2018/8/7.
 */
public class StatementClientFactory extends BasePooledObjectFactory<StatementClient> {

    private String livyAddress;
    private Properties prop;

    public StatementClientFactory(String livyAddress, Properties prop) {
        this.livyAddress = livyAddress;
        this.prop = prop;
    }

    @Override
    public StatementClient create() throws Exception {
        //        statementClient.waitSessionOk();
        return new StatementClient(new URI(livyAddress), new HttpConf(prop));
    }

    @Override
    public PooledObject<StatementClient> wrap(StatementClient statementClient) {
        return new DefaultPooledObject<>(statementClient);
    }

    @Override
    public boolean validateObject(PooledObject<StatementClient> p) {
        return p.getObject().validateSession();
    }

    @Override
    public void activateObject(PooledObject<StatementClient> p) throws Exception {
        super.activateObject(p);
    }

    /**
     * No-op.
     *
     * @param p ignored
     */
    @Override
    public void destroyObject(PooledObject<StatementClient> p) throws Exception {
        super.destroyObject(p);
        try {
            p.getObject().stop(true);
        } catch (Exception e) {

        }
    }
}
