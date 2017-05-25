package py.pol.una.ii.pw.test;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import py.pol.una.ii.pw.data.ProductRepository;
import py.pol.una.ii.pw.model.Product;
import py.pol.una.ii.pw.rest.ProductResourceRESTService;
import py.pol.una.ii.pw.service.ProductRegistration;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.validation.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * Created by cristhianjbd on 23/05/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class ProductResourceRESTServiceTest {
    private static final String RESOURCE_PATH = "/products";

    private final Long ID1 = 1L;
    private final Integer PRICE1 = 1200;
    private final String DESCRIPCION1 = "descripcion1";
    private final String NAME1 = "name1";

    private final Long ID_NO_EXISTE = 3L;
    private final Long ID_EXISTE = 1L;

    private final String DESCRIPCION2 = "descripcion2";
    private final String NAME2 = "name2";
    private final Integer PRICE2 = 1400;

    @InjectMocks
    public static ProductResourceRESTService productResourceRESTService = new ProductResourceRESTService();

    @Mock
    private ProductRepository repository;

    @Mock
    ProductRegistration registration;

    @Mock
    private Validator validator;

    @Mock
    private Logger log;


    public static InMemoryRESTServer server;

    public static Response response;

    private Product producto = new Product();
    private Product productAcrear = new Product();


    @Before
    public void setUp() throws Exception {
        producto.setDescripcion(DESCRIPCION1);
        producto.setName(NAME1);
        producto.setPrice(PRICE1);
        producto.setId(ID1);
        when(repository.findById(ID1)).thenReturn(producto);
        when(repository.findById(ID_NO_EXISTE)).thenReturn(null);

        List<Product> productList = new ArrayList<Product>();
        productList.add(producto);
        when(repository.findAllOrderedByName()).thenReturn(productList);

        productAcrear.setDescripcion(DESCRIPCION2);
        productAcrear.setName(NAME2);
        productAcrear.setPrice(PRICE2);
        doNothing().when(registration).register(productAcrear);


    }

    @After
    public void apagar() {
        response.close();
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        server = InMemoryRESTServer.create(productResourceRESTService);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        server.close();
    }

    @Test
    public void listarProductosRetornaOk() throws Exception {
        response = server.newRequest(RESOURCE_PATH).request().get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void obtenerProductoRetornaOk() throws Exception{
        response = server.newRequest(RESOURCE_PATH +"/"  + String.valueOf(ID1)).request().get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void obtenerProductoRetornaNotFound() throws Exception{
        response = server.newRequest(RESOURCE_PATH +"/" + String.valueOf(ID_NO_EXISTE)).request().get();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void crearProductoRetornaOk() throws Exception{
        response = server.newRequest(RESOURCE_PATH).request().buildPost(Entity.json(productAcrear)).invoke();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void actualizarProductoRetornaOk() throws Exception{
        response = server.newRequest(RESOURCE_PATH +"/" + String.valueOf(ID_EXISTE)).request().buildPut(Entity.json(productAcrear)).invoke();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void eliminarProductoRetornaOk() throws Exception{
        response = server.newRequest(RESOURCE_PATH +"/" + String.valueOf(ID_EXISTE)).request().buildDelete().invoke();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void eliminarProductoRetornaNotFound() throws Exception{
        response = server.newRequest(RESOURCE_PATH +"/" + String.valueOf(ID_NO_EXISTE)).request().buildDelete().invoke();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }



}
