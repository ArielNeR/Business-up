using Business_Up.Visual;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Business_Up.Entidades
{
    [Serializable]
    public class Venta
    {
        private int numeroFactura;
        private Cliente cliente;
        private List<Carrito> productos;
        private bool pagado;
        private DateTime fecha;
        private string metodoPago;
        private byte[] factura;

        public Venta(int numeroFactura, Cliente cliente, List<Carrito> productos, bool pagado, DateTime fecha, string metodoPago, byte[] factura)
        {
            this.numeroFactura = numeroFactura;
            this.cliente = cliente;
            this.productos = productos;
            this.pagado = pagado;
            this.fecha = fecha;
            this.metodoPago = metodoPago;
            this.factura = factura;
        }

        public Cliente Cliente { get => cliente; set => cliente = value; }
        public List<Carrito> Productos { get => productos; set => productos = value; }
        public double Total { get => productos.Sum(x => x.Item.Precio * x.Cantidad); }
        public string DatoTotal { get => "Total: " + productos.Sum(x => x.Item.Precio * x.Cantidad); }
        public bool Pagado { get => pagado; set => pagado = value; }
        public DateTime Fecha { get => fecha; set => fecha = value; }
        public String DatoFecha { get => fecha.ToString("d"); }
        public string MetodoPago { get => metodoPago; set => metodoPago = value; }
        public int NumeroFactura { get => numeroFactura; set => numeroFactura = value; }
        public byte[] Factura { get => factura; set => factura = value; }
    }
}
