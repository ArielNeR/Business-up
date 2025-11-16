using System;
using System.Collections.Generic;
using System.Text;

namespace Business_Up.Entidades
{
    [Serializable]
    public class Inventario
    {
        private string nombre;
        private string tipo;
        private string unidadMedida;
        private double precio;
        private int cantidad;
        private string codigoBarra;

        public Inventario()
        {

        }

        public Inventario(string nombre, string tipoItem, string unidadMedida, double precio, int cantidad, string codigoBarra)
        {
            this.nombre = nombre;
            tipo = tipoItem;
            this.precio = precio;
            this.cantidad = cantidad;
            this.unidadMedida = unidadMedida;
            this.codigoBarra = codigoBarra;
        }

        public string Nombre { get => nombre; set => nombre = value; }
        public string Tipo { get => tipo; set => tipo = value; }
        public string UnidadMedida { get => unidadMedida; set => unidadMedida = value; }
        public double Precio { get => precio; set => precio = value; }
        public int Cantidad { get => cantidad; set => cantidad = value; }
        public string DatoPrecio { get { if (tipo.Equals("Producto")){ return $"Precio {unidadMedida}: " + precio; } else { return "Valor: " + precio; } } }
        public string DatoCantidad { get { if (tipo.Equals("Producto")) { return "Unidades: " + cantidad; } else { return string.Empty; } } }
        public string CodigoBarra { get => codigoBarra; set => codigoBarra = value; }
    }
}
