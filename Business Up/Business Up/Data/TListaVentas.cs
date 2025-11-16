using Business_Up.Entidades;
using System.Collections.Generic;
using System.Linq;

namespace Business_Up.Data
{
    public class TListaVentas
    {
        private static List<Venta> listaVentas = new List<Venta>();

        public static List<Venta> ListaVentas { get => listaVentas; set => listaVentas = value; }

        public static void Ingresar(Venta venta)
        {
            var resul = listaVentas.Any(x => x.NumeroFactura == venta.NumeroFactura);
            if (!resul)
            {
                listaVentas.Add(venta);
            }
            else
            {
                venta.NumeroFactura++;
                Ingresar(venta);
            }
        }

        public static Venta ObtenerVenta(int numFactura)
        {
            Venta resul = null;
            for (int i = 0; i < ListaVentas.Count; i++)
            {
                if (ListaVentas[i].NumeroFactura == numFactura)
                {
                    resul = ListaVentas[i];
                }
            }
            return resul;
        }

    }
}
