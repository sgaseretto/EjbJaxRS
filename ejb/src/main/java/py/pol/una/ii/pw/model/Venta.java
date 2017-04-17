package py.pol.una.ii.pw.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
@Table(name = "Venta")
@NamedQueries( {
		@NamedQuery( name = "Venta.listar", query = "SELECT u FROM Venta u" ),
		@NamedQuery( name = "Venta.tamano", query = "SELECT count(u) FROM Venta u" )})
public class Venta implements Serializable {
    /** Default value included to remove warning. Remove or modify at will. **/
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

	@NotNull
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "ventas_productos", joinColumns = @JoinColumn(name = "id_Venta"), inverseJoinColumns = @JoinColumn(name = "id_ProductoComprado"))
	private List<ProductoComprado> productos;


	@NotNull
    @ManyToOne
    @JoinColumn(name = "id_customer")
    private Customer customer;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public List<ProductoComprado> getProductos() {
		return productos;
	}

	public void setProductos(List<ProductoComprado> productos) {
		this.productos = productos;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Customer getCustomer() {
		return customer;
	}


}