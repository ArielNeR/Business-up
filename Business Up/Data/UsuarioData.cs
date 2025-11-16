using Business_Up.Entidades;
using System;
using System.Collections.Generic;
using System.Text;

namespace Business_Up.Data
{
    
    public class UsuarioData
    {
        private static Usuario usuarioActual = new Usuario();

        public static Usuario UsuarioActual { get => usuarioActual; set => usuarioActual = value; }
    }
}
