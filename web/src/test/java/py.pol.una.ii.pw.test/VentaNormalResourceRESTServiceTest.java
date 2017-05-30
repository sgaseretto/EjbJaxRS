package py.pol.una.ii.pw.test;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import py.pol.una.ii.pw.data.CompraRepository;
import py.pol.una.ii.pw.data.VentaRepository;
import py.pol.una.ii.pw.model.*;
import py.pol.una.ii.pw.rest.CompraNormalResourceRESTService;
import py.pol.una.ii.pw.rest.VentaNormalResourceRESTService;
import py.pol.una.ii.pw.service.CompraRegistration;
import py.pol.una.ii.pw.service.VentaRegistration;

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
 * Created by cristhianjbd on 26/05/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class VentaNormalResourceRESTServiceTest {
    private static final String RESOURCE_PATH = "/ventas";

    private final Long ID_CUSTOMER = 1L;
    private final String NAME_CUSTOMER = "name";
    private final String PHONE_NUMBER_CUSTOMER = "091231234";
    private final String EMAIL_CUSTOMER = "email.sda@hotmail.com";
    private final Float CUENTA_CUSTOMER = 10000f;

    private final Long ID_PRODUCTO = 1L;
    private final Integer PRICE_PRODUCTO = 1200;
    private final String DESCRIPCION_PRODUCTO = "descripcion";
    private final String NAME_PRODUCTO = "name producto";

    private final Long ID_VENTA = 1L;
    private final Long ID_VENTA_NO_EXISTE = 3L;

    private final Long ID_PRODUCTO_COMPRADO = 1L;
    private final Integer CANTIDAD_PRODUCTO_COMPRADO = 100;


    @InjectMocks
    public static VentaNormalResourceRESTService ventaNormalResourceRESTService = new VentaNormalResourceRESTService();

    @Mock
    private VentaRepository repository;

    @Mock
    VentaRegistration registration;

    @Mock
    private Validator validator;

    @Mock
    private Logger log;

    public static InMemoryRESTServer server;

    public static Response response;

    private Customer customer = new Customer();
    private Product product = new Product();
    private Venta venta = new Venta();
    private ProductoComprado productoComprado = new ProductoComprado();
    private Venta ventaAcrear = new Venta();

    @Before
    public void setUp() throws Exception {
        customer.setId(ID_CUSTOMER);
        customer.setEmail(EMAIL_CUSTOMER);
        customer.setName(NAME_CUSTOMER);
        customer.setPhoneNumber(PHONE_NUMBER_CUSTOMER);

        product.setId(ID_PRODUCTO);
        product.setPrice(PRICE_PRODUCTO);
        product.setName(NAME_PRODUCTO);
        product.setDescripcion(DESCRIPCION_PRODUCTO);

        productoComprado.setId(ID_PRODUCTO_COMPRADO);
        productoComprado.setCantidad(CANTIDAD_PRODUCTO_COMPRADO);
        productoComprado.setProducto(product);

        List<ProductoComprado> productCompradoList = new ArrayList<ProductoComprado>();

        venta.setId(ID_VENTA);
        venta.setCustomer(customer);
        venta.setProductos(productCompradoList);

        when(repository.findById(ID_VENTA)).thenReturn(venta);
        when(repository.findById(ID_VENTA_NO_EXISTE)).thenReturn(null);

        List<Venta> compraList = new ArrayList<Venta>();
        compraList.add(venta);
        when(repository.findAllOrderedById()).thenReturn(compraList);


        ventaAcrear.setProductos(productCompradoList);
        ventaAcrear.setCustomer(customer);
        doNothing().when(registration).registerVenta(ventaAcrear);

    }

    @After
    public void apagar() {
        response.close();
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        server = InMemoryRESTServer.create(ventaNormalResourceRESTService);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        server.close();
    }

    @Test
    public void listarComprasRetornaOk() throws Exception {
        response = server.newRequest(RESOURCE_PATH).request().get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void obtenerCompraRetornaOk() throws Exception{
        response = server.newRequest(RESOURCE_PATH +"/"  + String.valueOf(ID_VENTA)).request().get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void obtenerVentaCustomerName() throws Exception{
        response = server.newRequest(RESOURCE_PATH +"/"  + String.valueOf(ID_VENTA)).request().get();
        Venta objetoRespuesta = response.readEntity(Venta.class);
        Assert.assertEquals(venta.getCustomer().getName(), objetoRespuesta.getCustomer().getName());
    }

    @Test
    public void obtenerCompraRetornaNotFound() throws Exception{
        response = server.newRequest(RESOURCE_PATH +"/" + String.valueOf(ID_VENTA_NO_EXISTE)).request().get();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void crearCompraRetornaOk() throws Exception{
        response = server.newRequest(RESOURCE_PATH).request().buildPost(Entity.json(ventaAcrear)).invoke();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void eliminarCompraRetornaOk() throws Exception{
        response = server.newRequest(RESOURCE_PATH +"/" + String.valueOf(ID_VENTA)).request().buildDelete().invoke();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void eliminarCompraoRetornaNotFound() throws Exception{
        response = server.newRequest(RESOURCE_PATH +"/" + String.valueOf(ID_VENTA_NO_EXISTE)).request().buildDelete().invoke();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }
/*
    @Test
    public void datosCompra()  throws Exception{
        response = server.newRequest(RESOURCE_PATH +"/"  + String.valueOf(ID_VENTA)).request().get();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }
*/

}
