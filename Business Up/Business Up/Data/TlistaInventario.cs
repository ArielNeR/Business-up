using Business_Up.Entidades;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Business_Up.Data
{
    public class TlistaInventario
    {
        private static List<Inventario> inventario = new List<Inventario>();
        private static List<Producto> listaProductos = new List<Producto>();
        private static List<Servicio> listaServicios = new List<Servicio>();

        public static List<Inventario> Inventario { get => inventario; set => inventario = value; }
        public static List<Producto> ListaProductos { get => listaProductos; set => listaProductos = value; }
        public static List<Servicio> ListaServicios { get => listaServicios; set => listaServicios = value; }

        public static bool IngresarInventario(object item)
        {
            if (item is Producto)
            {
                var pro = (Producto)item;
                var resul = inventario.Any(x => x.Nombre == pro.Nombre);
                if (!resul)
                {
                    listaProductos.Add(pro);
                    inventario.Add(new Inventario(pro.Nombre, "Producto", pro.UnidadMedida, pro.PrecioVenta, pro.CantidadTotal, pro.CodigoBarra));
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                if (item is Servicio)
                {
                    Servicio ser = (Servicio)item;
                    var resul = inventario.Any(x => x.Nombre == ser.Nombre);
                    if (!resul)
                    {
                        listaServicios.Add(ser);
                        inventario.Add(new Inventario(ser.Nombre, "Servicio", null, ser.Valor, 0, ser.CodigoBarra));
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
                else
                    return false;
            }
        }

        public static object BuscarItem(string dato)
        {
            object resul = null;
            for (int i = 0; i < inventario.Count; i++)
            {
                if (inventario[i].Nombre == dato)
                {
                    if (inventario[i].Tipo.Equals("Producto"))
                    {
                        foreach (var item in listaProductos)
                        {
                            if(item.Nombre == inventario[i].Nombre)
                            {
                                resul = item;
                            }
                        }
                    }
                    else
                    {
                        foreach (var item in listaServicios)
                        {
                            if (item.Nombre == inventario[i].Nombre)
                            {
                                resul = item;
                            }
                        }
                    }
                }
            }
            return resul;
        }

        public static Inventario GetItem(string dato)
        {
            Inventario resul = null;
            for (int i = 0; i < inventario.Count; i++)
            {
                if (inventario[i].Nombre == dato)
                {
                    resul = inventario[i];
                }
            }
            return resul;
        }

    }
}
