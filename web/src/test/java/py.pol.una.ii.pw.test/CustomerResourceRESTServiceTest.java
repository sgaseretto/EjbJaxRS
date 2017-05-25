package py.pol.una.ii.pw.test;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import py.pol.una.ii.pw.data.CustomerRepository;
import py.pol.una.ii.pw.data.ProviderRepository;
import py.pol.una.ii.pw.model.Customer;
import py.pol.una.ii.pw.model.Provider;
import py.pol.una.ii.pw.rest.CustomerResourceRESTService;
import py.pol.una.ii.pw.rest.ProviderResourceRESTService;
import py.pol.una.ii.pw.service.CustomerRegistration;
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
public class CustomerResourceRESTServiceTest {
    private static final String RESOURCE_PATH = "/customers";

    private final Long ID1 = 1L;
    private final String NAME1 = "name";
    private final String PHONE_NUMBER1 = "091231234";
    private final String EMAIL1 = "email.sda@hotmail.com";
    private final Float CUENTA1 = 10000f;

    private final Long ID_NO_EXISTE = 3L;
    private final Long ID_EXISTE = 1L;

    private final String PHONE_NUMBER2 = "021020102";
    private final String NAME2 = "name2";
    private final String EMAIL2 = "email.asd@gmail.com";
    private final Float CUENTA2 = 12000f;

    @InjectMocks
    public static CustomerResourceRESTService customerResourceRESTService  = new CustomerResourceRESTService();

    @Mock
    private CustomerRepository repository;

    @Mock
    CustomerRegistration registration;

    @Mock
    private Validator validator;

    @Mock
    private Logger log;


    public static InMemoryRESTServer server;

    public static Response response;

    private Customer customer = new Customer();
    private Customer customerAcrear = new Customer();


    @Before
    public void setUp() throws Exception {
        customer.setEmail(EMAIL1);
        customer.setName(NAME1);
        customer.setPhoneNumber(PHONE_NUMBER1);
        customer.setCuenta(CUENTA1);
        customer.setId(ID1);
        when(repository.findById(ID1)).thenReturn(customer);
        when(repository.findById(ID_NO_EXISTE)).thenReturn(null);

        List<Customer> customerList = new ArrayList<Customer>();
        customerList.add(customer);
        when(repository.findAllOrderedByName()).thenReturn(customerList);

        customerAcrear.setName(NAME2);
        customerAcrear.setEmail(EMAIL2);
        customerAcrear.setPhoneNumber(PHONE_NUMBER2);
        customerAcrear.setCuenta(CUENTA2);
        doNothing().when(registration).register(customerAcrear);


    }

    @After
    public void apagar() {
        response.close();
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        server = InMemoryRESTServer.create(customerResourceRESTService);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        server.close();
    }

    @Test
    public void listarCustomersRetornaOk() throws Exception {
        response = server.newRequest(RESOURCE_PATH).request().get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void obtenerCustomerRetornaOk() throws Exception{
        response = server.newRequest(RESOURCE_PATH +"/"  + String.valueOf(ID1)).request().get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void obtenerCustomerRetornaNotFound() throws Exception{
        response = server.newRequest(RESOURCE_PATH +"/" + String.valueOf(ID_NO_EXISTE)).request().get();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void crearCustomerRetornaOk() throws Exception{
        response = server.newRequest(RESOURCE_PATH).request().buildPost(Entity.json(customerAcrear)).invoke();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void actualizarCustomerRetornaOk() throws Exception{
        response = server.newRequest(RESOURCE_PATH +"/" + String.valueOf(ID_EXISTE)).request().buildPut(Entity.json(customerAcrear)).invoke();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void eliminarCustomerRetornaOk() throws Exception{
        response = server.newRequest(RESOURCE_PATH +"/" + String.valueOf(ID_EXISTE)).request().buildDelete().invoke();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void eliminarCustomerRetornaNotFound() throws Exception{
        response = server.newRequest(RESOURCE_PATH +"/" + String.valueOf(ID_NO_EXISTE)).request().buildDelete().invoke();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }



}
