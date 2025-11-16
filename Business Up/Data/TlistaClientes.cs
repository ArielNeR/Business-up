using Business_Up.Entidades;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Business_Up.Data
{
    public class TlistaClientes
    {
        private static List<Cliente> listaClientes = new List<Cliente>();

        public static List<Cliente> ListaClientes { get => listaClientes; set => listaClientes = value; }

        /**
        Este método recibe un objeto Cliente y verifica si ya existe un Cliente con el mismo nombre en la lista de clientes.
        Si no existe, se agrega el objeto Cliente a la lista y devuelve true. Si ya existe un Cliente con el mismo nombre,
        devuelve false.
        @param cl El objeto Cliente que se desea ingresar a la lista de clientes
        @return Devuelve true si se ingresó el objeto Cliente exitosamente, false si ya existe un Cliente con el mismo nombre en la lista.
        */
        public static bool Ingresar(Cliente cl)
        {
            var resul = listaClientes.Any(x => x.Nombre == cl.Nombre);
            if (!resul)
            {
                listaClientes.Add(cl);
                return true;
            }
            else
            {
                return false;
            }
        }

        /**
        Busca un cliente en la lista de clientes por su nombre.
        @param dato El nombre del cliente a buscar.
        @return El objeto Cliente encontrado o null si no existe en la lista.
        */
        public static Cliente BuscarCliente(string dato)
        {
            Cliente resul = null;
            for (int i = 0; i < listaClientes.Count; i++)
            {
                if(listaClientes[i].Nombre == dato)
                {
                    resul = listaClientes[i];
                }
            }
            return resul;
        }

    }
}
