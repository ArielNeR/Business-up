using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using Xamarin.Forms;

namespace Business_Up.Entidades
{
    [Serializable]
    public class Publicacion
    {
        private Usuario usuario;
        private int duracion;
        private byte[] imagen;
        private string titulo;
        private string descripcion;

        public Publicacion(Usuario usuario, int duracion, byte[] imagen, string titulo, string descripcion)
        {
            this.usuario = usuario;
            this.duracion = duracion;
            this.imagen = imagen;
            this.titulo = titulo;
            this.descripcion = descripcion;
        }

        public Usuario Usuario { get => usuario; set => usuario = value; }
        public int Duracion { get => duracion; set => duracion = value; }
        public string DuracionVisual { get => TimeSpan.FromSeconds(duracion).ToString(@"mm\:ss"); }
        public ImageSource ImagenVisual { get => ImageSource.FromStream(() => new MemoryStream(imagen)); }
        public string Titulo { get => titulo; set => titulo = value; }
        public string Descripcion { get => descripcion; set => descripcion = value; }
        public byte[] Imagen { get => imagen; set => imagen = value; }
    }
}
