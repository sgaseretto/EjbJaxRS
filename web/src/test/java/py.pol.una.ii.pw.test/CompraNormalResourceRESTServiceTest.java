package py.pol.una.ii.pw.test;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import py.pol.una.ii.pw.data.CompraRepository;
import py.pol.una.ii.pw.data.ProviderRepository;
import py.pol.una.ii.pw.model.Compra;
import py.pol.una.ii.pw.model.Product;
import py.pol.una.ii.pw.model.ProductoComprado;
import py.pol.una.ii.pw.model.Provider;
import py.pol.una.ii.pw.rest.CompraNormalResourceRESTService;
import py.pol.una.ii.pw.rest.ProviderResourceRESTService;
import py.pol.una.ii.pw.service.CompraRegistration;
import py.pol.una.ii.pw.service.ProviderRegistration;

import javax.persistence.criteria.CriteriaBuilder;
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
public class CompraNormalResourceRESTServiceTest {
    private static final String RESOURCE_PATH = "/compras";


    private final Long ID_PROVIDER = 1L;
    private final String NAME_PPOVIDER = "name";
    private final String PHONE_NUMBER_PROVIDER = "091231234";
    private final String EMAIL_PROVIDER = "email.sda@hotmail.com";

    private final Long ID_PRODUCTO = 1L;
    private final Integer PRICE_PRODUCTO = 1200;
    private final String DESCRIPCION_PRODUCTO = "descripcion";
    private final String NAME_PRODUCTO = "name producto";

    private final Long ID_COMPRA = 1L;
    private final Long ID_COMPRA_NO_EXISTE = 3L;

    private final Long ID_PRODUCTO_COMPRADO = 1L;
    private final Integer CANTIDAD_PRODUCTO_COMPRADO = 100;


    @InjectMocks
    public static CompraNormalResourceRESTService compraResourceRESTService = new CompraNormalResourceRESTService();

    @Mock
    private CompraRepository repository;

    @Mock
    CompraRegistration registration;

    @Mock
    private Validator validator;

    @Mock
    private Logger log;


    public static InMemoryRESTServer server;

    public static Response response;

    private Provider provider = new Provider();
    private Product product = new Product();
    private Compra compra = new Compra();
    private ProductoComprado productoComprado = new ProductoComprado();
    private Compra compraAcrear = new Compra();
    List<Compra> compraList = new ArrayList<Compra>();

    @Before
    public void setUp() throws Exception {
        provider.setId(ID_PROVIDER);
        provider.setEmail(EMAIL_PROVIDER);
        provider.setName(NAME_PPOVIDER);
        provider.setPhoneNumber(PHONE_NUMBER_PROVIDER);

        product.setId(ID_PRODUCTO);
        product.setPrice(PRICE_PRODUCTO);
        product.setName(NAME_PRODUCTO);
        product.setDescripcion(DESCRIPCION_PRODUCTO);

        productoComprado.setId(ID_PRODUCTO_COMPRADO);
        productoComprado.setCantidad(CANTIDAD_PRODUCTO_COMPRADO);
        productoComprado.setProducto(product);

        List<ProductoComprado> productCompradoList = new ArrayList<ProductoComprado>();

        compra.setId(ID_COMPRA);
        compra.setProvider(provider);
        compra.setProductos(productCompradoList);

        when(repository.findById(ID_COMPRA)).thenReturn(compra);
        when(repository.findById(ID_COMPRA_NO_EXISTE)).thenReturn(null);


        compraList.add(compra);
        when(repository.findAllOrderedByName()).thenReturn(compraList);


        compraAcrear.setProductos(productCompradoList);
        compraAcrear.setProvider(provider);
        doNothing().when(registration).registerCompra(compraAcrear);

    }

    @After
    public void apagar() {
        response.close();
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        server = InMemoryRESTServer.create(compraResourceRESTService);
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
        response = server.newRequest(RESOURCE_PATH +"/"  + String.valueOf(ID_COMPRA)).request().get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        response.close();
    }

    @Test
    public void obtenerCompraID() throws Exception{
        response = server.newRequest(RESOURCE_PATH +"/"  + String.valueOf(ID_COMPRA)).request().get();
        Compra objetoRespuesta = response.readEntity(Compra.class);
        Assert.assertEquals(compra.getId(), objetoRespuesta.getId());
        response.close();
    }

    @Test
    public void obtenerCompraRetornaNotFound() throws Exception{
        response = server.newRequest(RESOURCE_PATH +"/" + String.valueOf(ID_COMPRA_NO_EXISTE)).request().get();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void crearCompraRetornaOk() throws Exception{
        response = server.newRequest(RESOURCE_PATH).request().buildPost(Entity.json(compraAcrear)).invoke();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void eliminarCompraRetornaOk() throws Exception{
        response = server.newRequest(RESOURCE_PATH +"/" + String.valueOf(ID_COMPRA)).request().buildDelete().invoke();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void eliminarCompraoRetornaNotFound() throws Exception{
        response = server.newRequest(RESOURCE_PATH +"/" + String.valueOf(ID_COMPRA_NO_EXISTE)).request().buildDelete().invoke();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

}
