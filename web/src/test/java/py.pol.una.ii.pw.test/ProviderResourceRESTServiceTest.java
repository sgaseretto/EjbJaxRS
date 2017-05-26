package py.pol.una.ii.pw.test;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import py.pol.una.ii.pw.data.ProviderRepository;
import py.pol.una.ii.pw.model.Provider;
import py.pol.una.ii.pw.rest.ProviderResourceRESTService;
import py.pol.una.ii.pw.service.ProviderRegistration;

import javax.validation.Validator;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * Created by cristhianjbd on 24/05/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class ProviderResourceRESTServiceTest {
    private static final String RESOURCE_PATH = "/providers";

    private final Long ID1 = 1L;
    private final String NAME1 = "name";
    private final String PHONE_NUMBER1 = "091231234";
    private final String EMAIL1 = "email.sda@hotmail.com";

    private final Long ID_NO_EXISTE = 3L;
    private final Long ID_EXISTE = 1L;

    private final String PHONE_NUMBER2 = "021020102";
    private final String NAME2 = "name2";
    private final String EMAIL2 = "email.asd@gmail.com";

    @InjectMocks
    public static ProviderResourceRESTService providerResourceRESTService = new ProviderResourceRESTService();

    @Mock
    private ProviderRepository repository;

    @Mock
    ProviderRegistration registration;

    @Mock
    private Validator validator;

    @Mock
    private Logger log;


    public static InMemoryRESTServer server;

    public static Response response;

    private Provider provider = new Provider();
    private Provider providerAcrear = new Provider();


    @Before
    public void setUp() throws Exception {
        provider.setEmail(EMAIL1);
        provider.setName(NAME1);
        provider.setPhoneNumber(PHONE_NUMBER1);
        provider.setId(ID1);
        when(repository.findById(ID1)).thenReturn(provider);
        when(repository.findById(ID_NO_EXISTE)).thenReturn(null);

        List<Provider> providerList = new ArrayList<Provider>();
        providerList.add(provider);
        when(repository.findAllOrderedByName()).thenReturn(providerList);

        providerAcrear.setName(NAME2);
        providerAcrear.setEmail(EMAIL2);
        providerAcrear.setPhoneNumber(PHONE_NUMBER2);
        doNothing().when(registration).register(providerAcrear);


    }

    @After
    public void apagar() {
        response.close();
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        server = InMemoryRESTServer.create(providerResourceRESTService);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        server.close();
    }

    @Test
    public void listarProvidersRetornaOk() throws Exception {
        response = server.newRequest(RESOURCE_PATH).request().get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void obtenerProviderRetornaOk() throws Exception{
        response = server.newRequest(RESOURCE_PATH +"/"  + String.valueOf(ID1)).request().get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void obtenerProviderRetornaNotFound() throws Exception{
        response = server.newRequest(RESOURCE_PATH +"/" + String.valueOf(ID_NO_EXISTE)).request().get();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void crearProviderRetornaOk() throws Exception{
        response = server.newRequest(RESOURCE_PATH).request().buildPost(Entity.json(providerAcrear)).invoke();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void actualizarProviderRetornaOk() throws Exception{
        response = server.newRequest(RESOURCE_PATH +"/" + String.valueOf(ID_EXISTE)).request().buildPut(Entity.json(providerAcrear)).invoke();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void eliminarProviderRetornaOk() throws Exception{
        response = server.newRequest(RESOURCE_PATH +"/" + String.valueOf(ID_EXISTE)).request().buildDelete().invoke();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void eliminarProviderRetornaNotFound() throws Exception{
        response = server.newRequest(RESOURCE_PATH +"/" + String.valueOf(ID_NO_EXISTE)).request().buildDelete().invoke();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }



}
