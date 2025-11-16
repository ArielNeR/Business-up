using System;
using System.Collections.Generic;
using System.Text;

namespace Business_Up.Entidades
{
    [Serializable]
    public class Servicio
    {
        private string nombre;
        private double valor;
        private string codigoBarra;

        public Servicio()
        {

        }

        public Servicio(string nombre, double valor, string codigoBarra)
        {
            this.nombre = nombre;
            this.valor = valor;
            this.codigoBarra = codigoBarra;
        }

        public string Nombre { get => nombre; set => nombre = value; }
        public double Valor { get => valor; set => valor = value; }
        public string CodigoBarra { get => codigoBarra; set => codigoBarra = value; }
    }
}
