using System;
using System.Collections.Generic;
using System.Text;

namespace Business_Up.Entidades
{
    [Serializable]
    public class Cliente
    {
        private string Idcliente;
        private string nombreCliente;
        private string[] numeroContacto;
        private CuentasBanco[] cuentaBanco;
        private string[] correo;

        public Cliente()
        {

        }

        public Cliente(string idcliente, string nombreCliente, string[] numeroContacto, CuentasBanco[] cuentaBanco, string[] correo)
        {
            Idcliente = idcliente;
            this.nombreCliente = nombreCliente;
            this.numeroContacto = numeroContacto;
            this.cuentaBanco = cuentaBanco;
            this.correo = correo;
        }

        public string IdCliente { get => Idcliente; set => Idcliente = value; }
        public string Nombre { get => nombreCliente; set => nombreCliente = value; }
        public string[] NumerosContacto { get => numeroContacto; set => numeroContacto = value; }
        public CuentasBanco[] CuentasBanco { get => cuentaBanco; set => cuentaBanco = value; }
        public string[] Correos { get => correo; set => correo = value; }
    }

    [Serializable]
    public class CuentasBanco
    {
        private string tipoCuenta;
        private string codigoCuenta;

        public CuentasBanco(string tipoCuenta, string codigoCuenta)
        {
            this.tipoCuenta = tipoCuenta;
            this.codigoCuenta = codigoCuenta;
        }

        public string TipoCuenta { get => tipoCuenta; set => tipoCuenta = value; }
        public string CodigoCuenta { get => codigoCuenta; set => codigoCuenta = value; }
    }
}
