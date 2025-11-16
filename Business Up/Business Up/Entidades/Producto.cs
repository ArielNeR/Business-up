using System;
using System.Collections.Generic;
using System.Text;

namespace Business_Up.Entidades
{
    [Serializable]
    public class Producto
    {
        private string nombre;
        private string unidadMedida;
        private double precioVenta;
        private double precioProveedor;
        private int cantidadTotal;
        private string codigoBarra;
        public Producto()
        {
            
        }

        public Producto(string nombre, string unidadMedida, double precioVenta, double precioProveedor, int cantidadTotal, string codigoBarra)
        {
            this.nombre = nombre;
            this.unidadMedida = unidadMedida;
            this.precioVenta = precioVenta;
            this.precioProveedor = precioProveedor;
            this.cantidadTotal = cantidadTotal;
            this.codigoBarra = codigoBarra;
        }

        public string Nombre { get => nombre; set => nombre = value; }
        public string UnidadMedida { get => unidadMedida; set => unidadMedida = value; }
        public double PrecioVenta { get => precioVenta; set => precioVenta = value; }
        public double PrecioProveedor { get => precioProveedor; set => precioProveedor = value; }
        public int CantidadTotal { get => cantidadTotal; set => cantidadTotal = value; }
        public string CodigoBarra { get => codigoBarra; set => codigoBarra = value; }
    }
}
