import ftp.FileServer;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

public class FileServerTest {
    FileServer ftpServer;

    @Before
    public void setUp() throws Exception {
        ftpServer = new FileServer();
    }

    @After
    public void tearDown() throws Exception {
        ftpServer = null;
    }

    @Test
    public void testLogin() throws Exception {
        String userToLogin = "Michael";
        boolean result = ftpServer.login(userToLogin);

        Assert.assertTrue(result);
    }

    @Test
    public void testLogoff() throws Exception {
        String username = "John";
        boolean loginSuccess = ftpServer.login(username);
        
        if(loginSuccess)
        {
            boolean logoffSuccess = ftpServer.logoff(username);
            Assert.assertTrue(logoffSuccess);
        }
        else
        {
            Assert.fail();
        }
    }

    @Test
    public void createUserFolder(){
        // Test private method using reflection
        Class[] parameters = new Class[1];
        parameters[0] = String.class;

        try
        {
            Method createUserFolderMethod = ftpServer.getClass().getDeclaredMethod("createUserFolder", parameters);
            createUserFolderMethod.setAccessible(true);
            boolean result = (boolean)createUserFolderMethod.invoke(ftpServer, "David");

            Assert.assertTrue(result);
        }
        catch (Exception ex)
        {
            Assert.fail();
        }
    }
}