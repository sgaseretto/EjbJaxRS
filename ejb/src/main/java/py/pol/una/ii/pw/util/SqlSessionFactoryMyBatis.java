package py.pol.una.ii.pw.util;


import java.io.IOException;
import java.io.Reader;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class SqlSessionFactoryMyBatis
{
    private static SqlSessionFactory factory;

    private SqlSessionFactoryMyBatis() {
    }

    static
    {
        Reader reader = null;
        try {
            reader = Resources.getResourceAsReader("myBatis-config.xml");
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        factory = new SqlSessionFactoryBuilder().build(reader);
    }

    public static SqlSessionFactory getSqlSessionFactory()
    {
        return factory;
    }
}