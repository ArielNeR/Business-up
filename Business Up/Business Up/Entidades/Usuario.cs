using System;
using System.Collections.Generic;
using System.Text;
using Xamarin.Forms;

namespace Business_Up.Entidades
{
    [Serializable]
    public class Usuario
    {
        private string nombre;
        private string contra;
        private string correo;
        private byte[] fotoPerfil;

        public Usuario()
        {

        }

        public Usuario(string nombre, string contra, string correo, byte[] fotoPerfil)
        {
            this.nombre = nombre;
            this.contra = contra;
            this.correo = correo;
            this.fotoPerfil = fotoPerfil;
        }

        public string Nombre { get => nombre; set => nombre = value; }
        public string Contra { get => contra; set => contra = value; }
        public string Correo { get => correo; set => correo = value; }
        public byte[] FotoPerfil { get => fotoPerfil; set => fotoPerfil = value; }
        public ImageSource Foto { get => ImageSource.FromStream(()=>new System.IO.MemoryStream(fotoPerfil));}

    }
}
