package py.pol.una.ii.pw.test;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import py.pol.una.ii.pw.data.PagoRepository;
import py.pol.una.ii.pw.model.Customer;
import py.pol.una.ii.pw.model.Pago;
import py.pol.una.ii.pw.rest.PagoResourceRESTService;
import py.pol.una.ii.pw.service.PagoRegistration;

import javax.validation.Validator;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * Created by cristhianjbd on 24/05/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class PagoResourceRESTServiceTest {
    private static final String RESOURCE_PATH = "/pagos";


    private final Long ID_CUSTOMER = 1L;
    private final String NAME_CUSTOMER = "name";
    private final String PHONE_NUMBER_CUSTOMER = "091231234";
    private final String EMAIL_CUSTOMER = "email.sda@hotmail.com";
    private final Float CUENTA_CUSTOMER = 10000f;

    private final Long ID_PAGO = 1L;
    private final Integer MONTO_PAGO = 10000;
    private final Date FECHA_PAGO =  new Date();

    private final Long ID_PAGO_NO_EXISTE = 3L;


    @InjectMocks
    public static PagoResourceRESTService pagoResourceRESTService  = new PagoResourceRESTService();

    @Mock
    private PagoRepository repository;

    @Mock
    PagoRegistration registration;

    @Mock
    private Validator validator;

    @Mock
    private Logger log;


    public static InMemoryRESTServer server;

    public static Response response;

    private Pago pago = new Pago();
    private Customer customer = new Customer();
    private Pago pagoAcrear = new Pago();


    @Before
    public void setUp() throws Exception {
        customer.setId(ID_CUSTOMER);
        customer.setCuenta(CUENTA_CUSTOMER);
        customer.setEmail(EMAIL_CUSTOMER);
        customer.setName(NAME_CUSTOMER);
        customer.setPhoneNumber(PHONE_NUMBER_CUSTOMER);

        pago.setId(ID_PAGO);
        pago.setCustomer(customer);
        pago.setFecha(FECHA_PAGO);
        pago.setMonto(MONTO_PAGO);
        when(repository.findById(ID_PAGO)).thenReturn(pago);
        when(repository.findById(ID_PAGO_NO_EXISTE)).thenReturn(null);
        List<Pago> pagoList = new ArrayList<Pago>();
        pagoList.add(pago);
        when(repository.findAllOrderedById()).thenReturn(pagoList);

        pagoAcrear.setCustomer(customer);
        pagoAcrear.setFecha(FECHA_PAGO);
        pagoAcrear.setMonto(MONTO_PAGO);
        doNothing().when(registration).register(pago);
    }


    @After
    public void apagar() {
        response.close();
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        server = InMemoryRESTServer.create(pagoResourceRESTService);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        server.close();
    }

    @Test
    public void listarPagosRetornaOk() throws Exception {
        response = server.newRequest(RESOURCE_PATH).request().get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void obtenerPagoRetornaOk() throws Exception{
        response = server.newRequest(RESOURCE_PATH +"/"  + String.valueOf(ID_PAGO)).request().get();
        assertEquals("El pago que desea obtener no existe",Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void obtenerPagoRetornaNotFound() throws Exception{
        response = server.newRequest(RESOURCE_PATH +"/" + String.valueOf(ID_PAGO_NO_EXISTE)).request().get();
        assertEquals("El pago si existe",Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void crearPagoRetornaOk() throws Exception{
        response = server.newRequest(RESOURCE_PATH).request().buildPost(Entity.json(pagoAcrear)).invoke();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }


}
