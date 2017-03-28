package py.pol.una.ii.pw.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;


@Entity
@XmlRootElement
@Table(name = "Compra")
@NamedQueries( {
		@NamedQuery( name = "Compra.listar", query = "SELECT u FROM Compra u" ),
		@NamedQuery( name = "Compra.tamano", query = "SELECT count(u) FROM Compra u" )})
public class Compra implements Serializable {
    /** Default value included to remove warning. Remove or modify at will. **/
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

	@NotNull
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "compras_productos", joinColumns = @JoinColumn(name = "id_Compra"), inverseJoinColumns = @JoinColumn(name = "id_ProductoComprado"))
	private List<ProductoComprado> productos;
    
    @ManyToOne
    @JoinColumn(name = "id_provider")
    private Provider provider;
    

    public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

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


    
    

    
    

}