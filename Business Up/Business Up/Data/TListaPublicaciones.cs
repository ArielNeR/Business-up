using Business_Up.Entidades;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Business_Up.Data
{
    public class TListaPublicaciones
    {
        private static List<Publicacion> listaPublicaciones = new List<Publicacion>();

        public static List<Publicacion> ListaPublicaciones { get => listaPublicaciones; set => listaPublicaciones = value; }

        public static bool Publicar(Publicacion pb)
        {
            var resul = listaPublicaciones.Any(x => x.Titulo == pb.Titulo);
            if (!resul)
            {
                listaPublicaciones.Add(pb);
                return true;
            }
            else
            {
                return false;
            }
        }

        public static Publicacion GetPublicacion(string usuario, string titulo)
        {
            Publicacion resul = null;
            for (int i = 0; i < listaPublicaciones.Count; i++)
            {
                if (listaPublicaciones[i].Usuario.Nombre == usuario && listaPublicaciones[i].Titulo == titulo)
                {
                    resul = listaPublicaciones[i];
                }
            }
            return resul;
        }

        public static void DeletePublicacion(string usuario, string titulo)
        {
            for (int i = 0; i < listaPublicaciones.Count; i++)
            {
                if (listaPublicaciones[i].Usuario.Nombre == usuario && listaPublicaciones[i].Titulo == titulo)
                {
                    listaPublicaciones.Remove(listaPublicaciones[i]);
                }
            }
        }

    }
}
