using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading;
using System.Threading.Tasks;

using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace Business_Up.Visual
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class AgregarCliente : ContentPage
    {
        Thread hilo;
        private bool EstadoModificacion = false;
        public AgregarCliente(Entidades.Cliente cl)
        {
            InitializeComponent();
            cerrarNotificacion.Text = "\u2192";
            Mensaje.TranslateTo(-300, 100, 0);
            if(!(cl is null))
                Editar(cl);
        }

        /// <summary>
        /// Método que se encarga de mostrar los datos de un cliente en la interfaz gráfica de usuario para permitir su edición.
        /// </summary>
        /// <param name="cliente">Objeto de la clase Cliente con los datos del cliente a editar.</param>
        private void Editar(Entidades.Cliente cliente)
        {
            Titulo.Text = "Modificar Cliente";
            codigotxt.Text = cliente.IdCliente;
            nombretxt.Text = cliente.Nombre;
            Agregar.CommandParameter = cliente.Nombre;
            numeroContactotxt.Text = cliente.NumerosContacto[0];
            cuentaBancotxt.Text = cliente.CuentasBanco[0].CodigoCuenta;
            correotxt.Text = cliente.Correos[0];
            EstadoModificacion = true;
        }

        /// <summary>
        /// Método que retorna a la interfaz visual principal.
        /// </summary>
        private void BotonAtras_Clicked(object sender, EventArgs e)
        {
            Application.Current.MainPage.Navigation.PopModalAsync();
        }

        /**
        Método para agregar o modificar un cliente en la lista de clientes.
        Se validan los datos ingresados por el usuario y se procede a modificar o agregar un cliente según corresponda.
        Si el estado de modificación es verdadero, se modifican los datos del cliente que se ha pasado como parámetro.
        Si el estado de modificación es falso, se crea un nuevo objeto Cliente y se ingresa en la lista de clientes.
        Se valida que se haya ingresado un correo electrónico válido y que se hayan rellenado todos los campos.
        Si alguna validación no es satisfactoria, se muestra una notificación con el mensaje correspondiente.
        Después de modificar o agregar el cliente, se guardan los datos locales y se guarda la información en la base de datos.
        */
        private async void Agregar_Clicked(object sender, EventArgs e)
        {
            if (Validacion())
            {
                if (ValidacionCorreo())
                {
                    if (EstadoModificacion)
                    {
                        var res = Data.TlistaClientes.BuscarCliente(Agregar.CommandParameter.ToString());
                        res.IdCliente = codigotxt.Text;
                        res.Nombre = nombretxt.Text;
                        res.NumerosContacto[0] = numeroContactotxt.Text;
                        res.CuentasBanco[0].CodigoCuenta = cuentaBancotxt.Text;
                        res.Correos[0] = correotxt.Text;
                        Data.TData.GuardarDatosLocal();
                        await Application.Current.MainPage.Navigation.PopModalAsync();
                        await Task.Run(Data.TData.GuardarDatos);
                    }
                    else
                    {
                        var cliente = new Entidades.Cliente(codigotxt.Text, nombretxt.Text, new string[] { numeroContactotxt.Text }, new Entidades.CuentasBanco[] { new Entidades.CuentasBanco("Corriente", cuentaBancotxt.Text) }, new string[] { correotxt.Text });
                        Data.TlistaClientes.Ingresar(cliente);
                        Data.TData.GuardarDatosLocal();
                        await Application.Current.MainPage.Navigation.PopModalAsync();
                        await Task.Run(Data.TData.GuardarDatos);
                    }
                }
                else
                {
                    Agregar.IsEnabled = false;
                    var tag = (string)cerrarNotificacion.CommandParameter;
                    if (tag is null || tag.Equals("false"))
                    {
                        cerrarNotificacion.CommandParameter = "true";
                        MensajeTexto.Text = "Ingrese un correo valido";
                        await Mensaje.LayoutTo(new Rectangle(Mensaje.X + 320, Mensaje.Y, Mensaje.Width, Mensaje.Height), 600);
                        hilo = new Thread(mostrarMensaje);
                        hilo.Start();
                    }
                }
            }
            else
            {
                Agregar.IsEnabled = false;
                var tag = (string)cerrarNotificacion.CommandParameter;
                if (tag is null || tag.Equals("false"))
                {
                    cerrarNotificacion.CommandParameter = "true";
                    MensajeTexto.Text = "Debe rellenar todos los campos";
                    await Mensaje.LayoutTo(new Rectangle(Mensaje.X + 320, Mensaje.Y, Mensaje.Width, Mensaje.Height), 600);
                    hilo = new Thread(mostrarMensaje);
                    hilo.Start();
                }
            }
            
        }

        /**
        Muestra un mensaje en pantalla durante un tiempo determinado.
        Después de pasado el tiempo, mueve el mensaje fuera de la pantalla y habilita
        el botón de Agregar.
        También utiliza un hilo para mostrar el mensaje en segundo plano.
        */
        private async void mostrarMensaje()
        {
            await Task.Delay(4000);
            Device.BeginInvokeOnMainThread(async() =>
            {
                await Mensaje.LayoutTo(new Rectangle(Mensaje.X - 320, Mensaje.Y, Mensaje.Width, Mensaje.Height), 400);
                Agregar.IsEnabled = true;
            });
            cerrarNotificacion.CommandParameter = "false";
            hilo.Abort();
        }

        private bool Validacion()
        {
            if (!string.IsNullOrEmpty(codigotxt.Text) && !string.IsNullOrEmpty(nombretxt.Text) && !string.IsNullOrEmpty(numeroContactotxt.Text) && !string.IsNullOrEmpty(cuentaBancotxt.Text) && !string.IsNullOrEmpty(correotxt.Text))
                return true;
            else
                return false;
        }

        private bool ValidacionCorreo()
        {
            var email = correotxt.Text;
            var isValidEmail = Regex.IsMatch(email, @"^([\w\.\-]+)@([\w\-]+)((\.(\w){2,3})+)$");

            if (!isValidEmail)
            {
                correotxt.BackgroundColor = Color.Red;
                return false;
            }
            else
            {
                correotxt.BackgroundColor = Color.WhiteSmoke;
                return true;
            }
        }

    }
}